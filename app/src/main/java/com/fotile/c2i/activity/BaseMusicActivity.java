package com.fotile.c2i.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.fotile.c2i.activity.music.base.BaseActivity;
import com.fotile.c2i.activity.music.message.FinishActivityMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 文件名称：BaseMusicActivity
 * 创建时间：2018/3/26 14:06
 * 文件作者：yaohx
 * 功能描述：音乐base
 */
public class BaseMusicActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    //关闭所有音乐相关界面
    @Subscribe
    public void onActivityFinish(FinishActivityMessage message) {
        if (message._class.getSimpleName().contains("BaseMusicActivity")) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public boolean updateBottomViewStatus() {
        return false;
    }

    @Override
    public boolean updateLeftViewStatus() {
        return false;
    }
    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            return true;
        }
        return false;
    }
}
