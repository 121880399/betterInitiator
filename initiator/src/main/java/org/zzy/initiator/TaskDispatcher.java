package org.zzy.initiator;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.UiThread;

import org.zzy.initiator.sort.TaskSortUtil;
import org.zzy.initiator.task.Task;
import org.zzy.initiator.task.TaskRunnable;
import org.zzy.initiator.utils.LogUtils;
import org.zzy.initiator.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 因为整个APP使用同一个Dispatcher，所以设计成单例
 * @作者 Zhouzhengyi
 * @创建日期 2019/7/28
 */
public class TaskDispatcher {

    private static volatile TaskDispatcher mInstance;

    /**
     * 开始时间
     */
    private long mStartTime;

    /**
     * 是否初始化
     */
    private volatile  boolean mHasInit;

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 是否是主进程
     */
    private volatile  boolean mIsMainProcess;

    /**
     * 存放所有的任务
     */
    private List<Task> mAllTasks = new ArrayList<>();

    private List<Class<? extends Task>> mClazzAllTask = new ArrayList<>();

    /**
     * 需要等待的Task,这里面的Task需要执行完毕以后，主线程才能接着执行
     */
    private List<Task> mNeedWaitTasks = new ArrayList<>();

    /**
     * 已经结束了的Task
     */
    private volatile List<Class<? extends Task>> mFinishedTasks = new ArrayList<>(100);

    /**
     * 需要等待的Task的数量
     */
    private AtomicInteger mNeedWaitCount = new AtomicInteger();

    /**
     * 主线程中执行的Task
     */
    private volatile List<Task> mMainThreadTask = new ArrayList<>();

    /**
     * 工作线程执行的Task和返回
     */
    private Map<Task,Future> mWorkThreadTask = new LinkedHashMap<>(8);


    /**
     * 依赖任务，key为被依赖的Task，value为依赖该Task的所有任务List
     */
    private HashMap<Class<? extends Task>,ArrayList<Task>> mDependedHashMap = new HashMap<>(8);

    private CountDownLatch mCountDownLatch;

    /**
     * 等待超时时间
     */
    private int mWaitTimeOut = 1;

    /**
     * 等超时时间单位
     */
    private TimeUnit mUnit = TimeUnit.MINUTES;

    /**
     * 是否取消
     */
    private volatile boolean isCancel;

    private TaskDispatcher(){
    }

    /**
     * 获取单例
     */
    public static TaskDispatcher getInstance(){
        if(mInstance == null){
            synchronized (TaskDispatcher.class){
                if(mInstance == null){
                    mInstance = new TaskDispatcher();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化启动器
     * @param context 这里的context记得传Application中的，否则会引起内存泄漏
     */
    public TaskDispatcher init(Context context){
        if(context != null){
            mHasInit = true;
            mContext = context;
            mIsMainProcess = Utils.isMainProcess(context);
        }
        return this;
    }

    /**
     * 设置等待超时时间
     */
    public TaskDispatcher setWaitOutTime(int timeOut,TimeUnit unit){
        mWaitTimeOut = timeOut;
        mUnit = unit;
        return this;
    }

    /**
     * 添加任务
     * @param task 任务
     */
    public TaskDispatcher addTask(Task task){
        if(task != null){
            collectDepends(task);
            mAllTasks.add(task);
            mClazzAllTask.add(task.getClass());
            if(needWait(task)){
                mNeedWaitTasks.add(task);
                mNeedWaitCount.getAndIncrement();
            }
        }
        return this;
    }

    /**
     * 取消所有未完成的任务
     */
    public void cancelAll(){
        //取消掉主线程中执行的任务
        isCancel = true;
        //取消掉工作线程中的任务
        //为了保证按顺序遍历，使用了LinkedHashMap而没有使用HashMap
        for(Entry<Task, Future> entry: mWorkThreadTask.entrySet()){
            Task key = entry.getKey();
            Future value = entry.getValue();
            if(!value.isDone()) {
                key.cancel();
                //取消未开始的任务和中断正在运行的任务
                boolean cancel = value.cancel(true);
            }
        }
    }

    private void collectDepends(Task task){
        if(mDependedHashMap == null){
            LogUtils.i("DependedHashMap is empty!");
            return;
        }
        if(task.dependsOn() != null && task.dependsOn().size() > 0 ){
            for(Class<? extends Task> clazz : task.dependsOn()){
                if(mDependedHashMap.get(clazz) == null){
                    mDependedHashMap.put(clazz,new ArrayList<Task>());
                }
                mDependedHashMap.get(clazz).add(task);
            }
        }
    }

    /**
     * 判断是否是异步线程需要等待的情况
     */
    private boolean needWait(Task task){
        return !task.runOnMainThread() && task.needWait();
    }

    @UiThread
    public void start(){
        if(!mHasInit){
            throw new RuntimeException("must call init first");
        }

        mStartTime = System.currentTimeMillis();
        if(Looper.getMainLooper() != Looper.myLooper()){
            throw new RuntimeException("must be called form UiThread");
        }
        if(mAllTasks.size() > 0 ){
            printDependedMsg();
            //对Task进行排序
            mAllTasks = TaskSortUtil.getSortResult(mAllTasks,mClazzAllTask);
            mCountDownLatch = new CountDownLatch(mNeedWaitCount.get());

            //运行在工作线程中要执行的任务,必须在执行主线程Task之前运行，
            //因为异步任务只需要发送给线程池，而如果先执行主线程的Task,那么
            //异步任务就需要一直等待
            executeTaskOnWorkThread();
            //运行在主线程中要执行的任务
            executeTaskOnMainThread();
        }
        await();
    }

    private void executeTaskOnWorkThread(){
        for(Task task : mAllTasks){
            //如果任务只能在主进程中执行，而当前进程为非主进程，
            //那么标记所有任务为完成
            if(task.onlyInMainProcess() && !mIsMainProcess){
                markTaskDone(task);
            }else{
                sendTask(task);
            }
            task.setSend(true);
        }
    }

    /**
     *  将任务标记成完成
     */
    public void markTaskDone(Task task){
            mFinishedTasks.add(task.getClass());
            if(task.needWait()){
                mCountDownLatch.countDown();
                mNeedWaitTasks.remove(task);
                mNeedWaitCount.getAndDecrement();
            }
    }

    private void sendTask(Task task){
        if(!task.runOnMainThread()){
            Future future = task.runOn().submit(new TaskRunnable(task,this));
            mWorkThreadTask.put(task,future);
        }else{
            mMainThreadTask.add(task);
        }
    }

    private void executeTaskOnMainThread(){
        mStartTime = System.currentTimeMillis();
        for(Task task : mMainThreadTask){
            if(isCancel){
                return;
            }
            long time = System.currentTimeMillis();
            new TaskRunnable(task,this).run();
            LogUtils.i(task.getClass().getSimpleName() + " cost "+(System.currentTimeMillis()-time));
        }
        LogUtils.i("Main Thread Task cost:"+(System.currentTimeMillis()-mStartTime));
    }

    private void await(){
        try {
            if (mNeedWaitCount.get() > 0) {
                mCountDownLatch.await(mWaitTimeOut, mUnit);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * 是否打开调试模式
     */
    public TaskDispatcher setDebug(boolean isDebug){
        LogUtils.setDebug(isDebug);
        return this;
    }

    /**
     * 查看被依赖的信息
     */
    private void printDependedMsg() {
        LogUtils.i("needWait size : " + (mNeedWaitCount.get()));
        if (LogUtils.isDebug()) {
            for (Class<? extends Task> cls : mDependedHashMap.keySet()) {
                LogUtils.i("cls " + cls.getSimpleName() + "   " + mDependedHashMap.get(cls).size());
                for (Task task : mDependedHashMap.get(cls)) {
                    LogUtils.i("cls       " + task.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * 通知后面的任务，前一个任务已经完成
     */
    public void notifyChildren(Task task){
        ArrayList<Task> tasks = mDependedHashMap.get(task.getClass());
        if(tasks !=null && tasks.size()>0){
            for (Task perTask:tasks) {
                perTask.countDown();
            }
        }
    }

    public boolean isMainProcess(){
        return mIsMainProcess;
    }

}
