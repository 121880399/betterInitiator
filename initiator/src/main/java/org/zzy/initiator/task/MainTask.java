package org.zzy.initiator.task;

/**
 * 运行在主线程的任务
 * @作者 ZhouZhengyi
 * @创建日期 2019/7/29
 */
public abstract class MainTask extends Task{
    @Override
    public boolean runOnMainThread() {
        return true;
    }
}
