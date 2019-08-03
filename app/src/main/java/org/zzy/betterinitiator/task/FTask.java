package org.zzy.betterinitiator.task;

import android.os.SystemClock;

import org.zzy.initiator.task.ITask;
import org.zzy.initiator.task.Task;
import org.zzy.initiator.task.TaskCallBack;
import org.zzy.initiator.utils.LogUtils;

/**
 * 在子线程中运行，使用IO线程池
 * @作者 Zhouzhengyi
 * @创建日期 2019/7/31
 */
public class FTask extends Task {

    private TaskCallBack<String> mTaskCallBack;

    public FTask(TaskCallBack callBack){
        this.mTaskCallBack = callBack;
    }

    @Override
    public void run() {
        LogUtils.i("FTask is running");
        SystemClock.sleep(1000);
        if(mTaskCallBack!=null){
            mTaskCallBack.result("success");
        }
    }

    @Override
    public boolean runOnMainThread() {
        return false;
    }

    @Override
    public int threadPoolType() {
        return ITask.IO;
    }

}
