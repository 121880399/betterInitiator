package org.zzy.betterinitiator.task;

import android.os.SystemClock;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.zzy.betterinitiator.event.InitEvent;
import org.zzy.initiator.task.Task;
import org.zzy.initiator.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ETask依赖于CTask和DTask
 * 在主线程中运行
 * ETask中需要传入BTask中返回的参数
 * @作者 Zhouzhengyi
 * @创建日期 2019/7/31
 */
public class ETask extends Task {

    String data;

    public ETask(){
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true)
    public void onInitEvent(InitEvent event){
        data = event.getResult();
    }

    @Override
    public void run() {
        LogUtils.i("ETask is running");
        if(!TextUtils.isEmpty(data)) {
            LogUtils.i("input data:" + data);
        }
        SystemClock.sleep(1000);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public List<Class<? extends Task>> dependsOn() {
        List<Class<? extends Task>> list = new ArrayList<>();
        list.add(CTask.class);
        list.add(DTask.class);
        return list;
    }

    @Override
    public boolean runOnMainThread() {
        return false;
    }


    @Override
    public boolean needWait() {
        return true;
    }

}
