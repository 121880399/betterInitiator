package org.zzy.initiator.utils;

import android.util.Log;

/**
 * @作者 ZhouZhengyi
 * @创建日期 2019/7/29
 */
public class LogUtils {
    private static boolean mDebug = false;

    public static void i(String msg){
        if(mDebug){
            Log.i("initiator",msg);
        }
    }

    public static boolean isDebug() {
        return mDebug;
    }

    public static void setDebug(boolean mDebug) {
        LogUtils.mDebug = mDebug;
    }
}
