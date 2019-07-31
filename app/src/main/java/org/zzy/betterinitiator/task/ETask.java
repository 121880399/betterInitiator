package org.zzy.betterinitiator.task;

import android.os.SystemClock;

import org.zzy.initiator.task.Task;
import org.zzy.initiator.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ETask依赖于CTask和DTask
 * 在主线程中运行
 * @作者 Zhouzhengyi
 * @创建日期 2019/7/31
 */
public class ETask extends Task {

    @Override
    public void run() {
        LogUtils.i("ETask is running");
        SystemClock.sleep(1000);
    }

    @Override
    public List<Class<? extends Task>> dependsOn() {
        List<Class<? extends Task>> list = new ArrayList<>();
        list.add(CTask.class);
        list.add(DTask.class);
        return list;
    }

}
