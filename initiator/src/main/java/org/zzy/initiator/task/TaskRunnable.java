package org.zzy.initiator.task;

import android.os.Looper;
import android.os.Process;

import org.zzy.initiator.TaskDispatcher;
import org.zzy.initiator.utils.LogUtils;

import java.util.concurrent.TimeUnit;

/**
 * 任务执行的地方
 * @作者 Zhouzhengyi
 * @创建日期 2019/7/29
 */
public class TaskRunnable implements Runnable {

    private Task mTask;

    private TaskDispatcher mTaskDispatcher;

    public TaskRunnable(Task task){
        mTask = task;
    }

    public TaskRunnable(Task task,TaskDispatcher dispatcher){
        mTask = task;
        mTaskDispatcher = dispatcher;
    }

    @Override
    public void run() {
        LogUtils.i(mTask.getClass().getSimpleName() + "begin run");
        Process.setThreadPriority(mTask.priority());

        long startTime = System.currentTimeMillis();
        long waitTime=0;
        if(mTask.dependsOn() != null){
            mTask.setWaiting(true);
            //1分钟超时
            mTask.await(1, TimeUnit.MINUTES);
            //得到等待时长
            waitTime = System.currentTimeMillis() - startTime;
        }
        if(mTask.isFinished()){
            return;
        }
        startTime = System.currentTimeMillis();
        //开始执行任务
        mTask.setRunning(true);
        mTask.run();
        if(mTask.isFinished()){
            return;
        }
        //执行尾部任务
        Runnable tailRunnable = mTask.getTailRunnable();
        if(tailRunnable != null ){
            tailRunnable.run();
        }
        if(!mTask.needCallBack() || !mTask.runOnMainThread()){
            printTaskLog(startTime,waitTime);
            mTask.setFinished(true);
            if(mTaskDispatcher != null){
                mTaskDispatcher.notifyChildren(mTask);
                mTaskDispatcher.markTaskDone(mTask);
            }
            LogUtils.i(mTask.getClass().getSimpleName() + "finish");
        }
    }

    /**
     * 打印出来Task执行的日志
     *
     * @param startTime
     * @param waitTime
     */
    private void printTaskLog(long startTime, long waitTime) {
        long runTime = System.currentTimeMillis() - startTime;
        if (LogUtils.isDebug()) {
            LogUtils.i(mTask.getClass().getSimpleName() + "  wait " + waitTime + "    run "
                    + runTime + "   isMain " + (Looper.getMainLooper() == Looper.myLooper())
                    + "  needWait " + (mTask.needWait() || (Looper.getMainLooper() == Looper.myLooper()))
                    + "  ThreadId " + Thread.currentThread().getId()
                    + "  ThreadName " + Thread.currentThread().getName()
            );
        }
    }
}
