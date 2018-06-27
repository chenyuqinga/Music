package com.fotile.c2i.activity.music.util;

import android.util.Log;


/**
 * 文件名称：LogUtil
 * 创建时间：2017/8/7 15:14
 * 文件作者：yaohx
 * 功能描述：项目全局打印日志
 */
public class LogUtil {

    public static void LOG_SCREEN(String obj) {

    }

    public static void LOG_RECIPE(String TAG, Object obj) {

    }

    public static void LOG_RECIPE_PLAY(String TAG, Object obj) {

    }

    public static void LOG_STOVE(String TAG, Object obj) {

    }

    public static void LOG_TOOTH(String TAG, Object obj) {

    }

    public static void LOG_POWER(String TAG, Object obj) {

    }


    public static void LOGE(String tag, Object obj) {

            if (null != obj) {
                Log.e(tag, obj.toString());
            } else {
                Log.e(tag, "null");
            }

    }

    public static void LOGD(String tag, Object obj) {

            if (null != obj) {
                Log.d(tag, obj.toString());
            } else {
                Log.d(tag, "null");
            }

    }

    public static void LOGSleepLock(String tag) {

            if (null != tag) {
                Log.e("----SleepLockTool----", tag);
            } else {
                Log.e("----SleepLockTool----", "null--");
            }
        }

}
