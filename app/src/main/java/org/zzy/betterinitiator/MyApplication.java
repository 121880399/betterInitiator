package org.zzy.betterinitiator;

import android.app.Application;

import org.zzy.betterinitiator.task.ATask;
import org.zzy.betterinitiator.task.BTask;
import org.zzy.betterinitiator.task.CTask;
import org.zzy.betterinitiator.task.DTask;
import org.zzy.betterinitiator.task.ETask;
import org.zzy.betterinitiator.task.FTask;
import org.zzy.initiator.TaskDispatcher;
import org.zzy.initiator.task.TaskCallBack;
import org.zzy.initiator.utils.LogUtils;

/**
 * @作者 admin
 * @创建日期 2019/7/31
 */
public class MyApplication extends Application {

    String str;

    @Override
    public void onCreate() {
        super.onCreate();
        TaskDispatcher taskDispatcher = TaskDispatcher.getInstance();
        //这里面模拟某个Task中初始化完成以后，需要有返回值给Application持有
        TaskCallBack<String> FTaskCallback =new TaskCallBack<String>() {

            @Override
            public void result(String data) {
                str = data;
                LogUtils.i(str);
            }
        };
        taskDispatcher.init(this)
                .addTask(new ATask())
                .addTask(new BTask())
                .addTask(new CTask())
                .addTask(new DTask())
                .addTask(new ETask())
                .addTask(new FTask(FTaskCallback))
                .start();
    }
}
