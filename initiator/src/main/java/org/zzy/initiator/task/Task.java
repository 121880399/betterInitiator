package org.zzy.initiator.task;

import android.os.CountDownTimer;
import android.os.Process;

import org.zzy.initiator.utils.DispatcherExecutor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 声明为抽象类，提供一些默认实现
 * @作者 ZhouZhengyi
 * @创建日期 2019/7/28
 */
public abstract class Task  implements ITask{
    /**
     * 是否正在等待
     */
    private volatile boolean mIsWaiting;
    /**
     * 是否正在执行
     */
    private volatile boolean mIsRunning;
    /**
     * Task是否执行完成
     */
    private volatile boolean mIsFinished;
    /**
     * Task是否已经被分发
     */
    private volatile boolean mIsSend;

    private CountDownLatch mDepends = new CountDownLatch(dependsOn() == null ? 0 : dependsOn().size());


    public void await(){
        try {
            mDepends.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * 带有超时，推荐使用
     * @param timeOut 超时时间
     * @param unit 单位
     */
    public void await(int timeOut,TimeUnit unit){
        try {
            //1分钟超时
            mDepends.await(timeOut, unit);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * 依赖执行完进行减一操作
     */
    public void countDown(){
        mDepends.countDown();
    }


    @Override
    public int priority() {
        return Process.THREAD_PRIORITY_BACKGROUND;
    }

    /**
     * 默认使用IO
     */
    @Override
    public int getThreadPoolType() {
        return ITask.IO;
    }

    @Override
    public ExecutorService runOn() {
        switch (getThreadPoolType()){
            case ITask.CPU:
                return DispatcherExecutor.getCPUExecutor();
            case ITask.IO:
                return DispatcherExecutor.getIOExecutor();
                default:
                    return DispatcherExecutor.getIOExecutor();
        }
    }


    /**
     * 默认不需要
     */
    @Override
    public boolean needWait() {
        return false;
    }

    @Override
    public List<Class<? extends Task>> dependsOn() {
        return null;
    }

    @Override
    public boolean runOnMainThread() {
        return false;
    }

    @Override
    public Runnable getTailRunnable() {
        return null;
    }

    @Override
    public void setTaskCallBack(TaskCallBack callBack) {

    }

    @Override
    public boolean needCallBack() {
        return false;
    }


    @Override
    public boolean onlyInMainProcess() {
        return true;
    }

    public  boolean isRunning(){
        return mIsRunning;
    }

    public void setRunning(boolean isRunning){
        this.mIsRunning = isRunning;
    }

    public boolean isWaiting() {
        return mIsWaiting;
    }

    public void setWaiting(boolean waiting) {
        mIsWaiting = waiting;
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    public void setFinished(boolean finished) {
        mIsFinished = finished;
    }

    public boolean isSend() {
        return mIsSend;
    }

    public void setSend(boolean send) {
        mIsSend = send;
    }
}
