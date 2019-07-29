package org.zzy.initiator.utils;

import android.os.Handler;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
 * @作者 ZhouZhengyi
 * @创建日期 2019/7/28
 */
public class DispatcherExecutor {
    /**
     * 适用于CPU密集型操作的线程池
     */
    private static ThreadPoolExecutor mCPUThreadPoolExecutor;

    /**
     * 适用于IO密集型操作的线程池
     */
    private static ExecutorService mIOThreadPoolExecutor;

    /**
     * CPU数量
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池核心线程数量
     */
    private static final int CORE_POOL_SIZE =  Math.max(2,Math.min(CPU_COUNT - 1,4));

    /**
     * 最大线程数量
     */
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 2 + 1;

    /**
     * 线程存活时间
     */
    private static final int KEEP_ALIVE_SECONDS = 30;

    /**
     * 线程工厂
     */
    private static final ThreadFactory mThreadFactory = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"TaskDispatcher #"+mCount.getAndIncrement());
        }
    };

    /**
     *  阻塞队列，设置为20的容量
     */
    private static final BlockingQueue<Runnable> mPoolWorkQueue = new LinkedBlockingQueue<>(20);

    /**
     * 线程池拒绝策略
     */
    private static final RejectedExecutionHandler mHandle = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            //新开一个线程池执行
            Executors.newCachedThreadPool().execute(r);
        }
    };

    static {
        mCPUThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,mPoolWorkQueue,mThreadFactory,mHandle);
        mCPUThreadPoolExecutor.allowCoreThreadTimeOut(true);
        mIOThreadPoolExecutor = Executors.newCachedThreadPool(mThreadFactory);
    }

    /**
     * 获取CPU线程池
     */
    public static ThreadPoolExecutor getCPUExecutor(){
        return mCPUThreadPoolExecutor;
    }

    /**
     * 获取IO线程池
     */
    public static ExecutorService getIOExecutor(){
        return mIOThreadPoolExecutor;
    }


}
