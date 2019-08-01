package org.zzy.betterinitiator.event;

/**
 * 初始化事件
 * @作者 Zhouzhengyi
 * @创建日期 2019/8/1
 */
public class InitEvent {

    private String result;

    public InitEvent(String str){
        result = str;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
