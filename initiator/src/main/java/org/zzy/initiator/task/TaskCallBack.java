package org.zzy.initiator.task;

/**
 * 任务回调接口
 * @作者 ZhouZhengyi
 * @创建日期 2019/7/29
 */
public interface TaskCallBack<T> {
    void result(T... data);
}
