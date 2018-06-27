package com.fotile.c2i.activity.music.base;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;



/**
 * 文件名称：BaseApplication
 * 创建时间：2017/8/7 13:39
 * 文件作者：yaohx
 * 功能描述：实现一些基本的初始化操作，不要阻塞线程
 */
public class BaseApplication extends Application {

    private boolean canPlayMusicScreen = false;
    @Override
    public void onCreate() {
        super.onCreate();

    }


    public boolean isCanPlayMusicScreen() {
        return canPlayMusicScreen;
    }

    public void setCanPlayMusicScreen(boolean canPlayMusicScreen) {
        this.canPlayMusicScreen = canPlayMusicScreen;
    }
}
