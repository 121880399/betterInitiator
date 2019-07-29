package org.zzy.initiator.task;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.os.Process;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务接口
 * @作者 ZhouZhengyi
 * @创建日期 2019/7/28
 */
public interface ITask {
    public static final int CPU = 1;

    public static final int IO = 2;
    @IntDef(value = {
            CPU,
            IO
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThreadPoolType {}

    /**
     * 任务的优先级，可以根据Task的重要程度进行指定，范围可以修改
     */
    @IntRange(from = Process.THREAD_PRIORITY_FOREGROUND , to = Process.THREAD_PRIORITY_LOWEST)
    int priority();

    void run();

    @ThreadPoolType
    int getThreadPoolType();

    /**
     * 执行Task
     */
    ExecutorService runOn();

    /**
     * Task之间依赖关系
     */
    List<Class<? extends Task>> dependsOn();

    /**
     * 异步线程执行的Task是否需要在调用await时进行等待，默认不需要
     */
    boolean needWait();

    /**
     * 是否在主线程中执行
     */
    boolean runOnMainThread();

    /**
     * 是否只是在主进程中执行
     */
    boolean onlyInMainProcess();

    /**
     * Task主任务执行完成之后需要执行的任务
     */
    Runnable getTailRunnable();

    /**
     * 设置Task回调
     */
    void setTaskCallBack(TaskCallBack callBack);

    /**
     * 是否需要回调
     */
    boolean needCallBack();
}
