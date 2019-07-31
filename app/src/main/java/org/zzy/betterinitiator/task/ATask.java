package org.zzy.betterinitiator.task;

import android.os.SystemClock;

import org.zzy.initiator.task.Task;
import org.zzy.initiator.utils.LogUtils;

/**
 * @作者 Zhouzhengyi
 * @创建日期 2019/7/31
 */
public class ATask extends Task {

    @Override
    public void run() {
        LogUtils.i("ATask is running");
        SystemClock.sleep(1000);
    }

}
