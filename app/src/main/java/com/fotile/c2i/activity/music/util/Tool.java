package com.fotile.c2i.activity.music.util;


/**
 * 文件名称：Tool
 * 创建时间：2017/8/15
 * 文件作者：wanghouyu
 * 功能描述：工具类包括二维码生成等
 */

public class Tool {
    public static boolean IS_ALARM_ACTIVITY_OPENED = false;

    private static long last_time = 0;

    /**
     * 限制按钮的快速点击情况
     *
     * @return true 执行click事件
     */
    public static boolean fastclick() {
        long time = System.currentTimeMillis();
        long interval = time - last_time;
        last_time = time;
        if (interval > 400) {
            return true;
        }
        return false;
    }

}
