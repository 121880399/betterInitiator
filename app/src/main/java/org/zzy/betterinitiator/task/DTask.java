package org.zzy.betterinitiator.task;

import android.os.SystemClock;

import org.zzy.initiator.task.ITask;
import org.zzy.initiator.task.Task;
import org.zzy.initiator.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DTask依赖于BTask
 * 在子线程中运行，使用IO线程池
 * @作者 Zhouzhengyi
 * @创建日期 2019/7/31
 */
public class DTask extends Task {

    @Override
    public void run() {
        LogUtils.i("DTask is running");
        SystemClock.sleep(1000);
    }

    @Override
    public List<Class<? extends Task>> dependsOn() {
        List<Class<? extends Task>> list = new ArrayList<>();
        list.add(BTask.class);
        return list;
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
