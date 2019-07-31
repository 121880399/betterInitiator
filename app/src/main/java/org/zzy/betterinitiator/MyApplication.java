package org.zzy.betterinitiator;

import android.app.Application;

import org.zzy.betterinitiator.task.ATask;
import org.zzy.betterinitiator.task.BTask;
import org.zzy.betterinitiator.task.CTask;
import org.zzy.betterinitiator.task.DTask;
import org.zzy.betterinitiator.task.ETask;
import org.zzy.betterinitiator.task.FTask;
import org.zzy.initiator.TaskDispatcher;

/**
 * @作者 admin
 * @创建日期 2019/7/31
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TaskDispatcher taskDispatcher = TaskDispatcher.getInstance();
        taskDispatcher.init(this)
                .addTask(new ATask())
                .addTask(new BTask())
                .addTask(new CTask())
                .addTask(new DTask())
                .addTask(new ETask())
                .addTask(new FTask())
                .start();

    }
}
