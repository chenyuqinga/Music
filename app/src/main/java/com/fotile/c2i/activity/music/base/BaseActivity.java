package com.fotile.c2i.activity.music.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import com.fotile.c2i.activity.music.MainActivity;
import com.fotile.c2i.activity.music.R;
import com.fotile.c2i.activity.music.util.AppManagerUtil;



/**
 * 文件名称：BaseActivity
 * 创建时间：2017/8/7 13:39
 * 文件作者：yaohx
 * 功能描述：所有Activity的基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 管理RxJava的订阅关系
     * CompositeSubscription.unsubscribe()，这个CompositeSubscription对象就不可用了
     * 如果要继续使用CompositeSubscription，就必须再创建一个新的对象了
     */
    public CompositeSubscription compositeSubscription;

    public int screen_width;

    public int screen_height;

    /**
     * Activity根布局
     */
    public ViewGroup rootView;
    /**
     * dock高度
     */
    public int bottomHeight;
    /**
     * 左边状态栏的宽度
     */
    public int leftWidth;
    /**
     * dock是否在顶部的标识
     */
    public boolean isDockTop;
    /**
     * 纪录根视图的显示高度
     */
    int rootViewVisibleHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setScreenMargin();
        AppManagerUtil.getInstance().addActivity(this);


        screen_width = getWindowManager().getDefaultDisplay().getWidth();
        screen_height = getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 在子类中将action1变量赋值
     */
    public void createAction() {

    }

    /**
     * 回收一些资源
     */
    public void recovery() {

    }

    /**
     * 在每一个Activity结束的时候负责解除订阅关系
     */
    @Override
    protected void onDestroy() {



        //移除当前Activity
        AppManagerUtil.getInstance().removeActivity(this);
        //回收资源
        recovery();

        super.onDestroy();
    }

    public abstract int getLayoutId();

    @Override
    protected void onResume() {
        super.onResume();
        updateBottomViewStatus();
        updateLeftViewStatus();

    }

    public void launch2Home() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void launchActivity(Class<?> activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
    }

    public void launchActivity(Intent intent) {
        startActivity(intent);
    }

    public void launchActivity(Class<?> activityClass, Intent intent) {
        intent.setClass(getApplicationContext(), activityClass);
        startActivity(intent);
    }

    /**
     * 更新底部导航栏的显示
     */
    public abstract boolean updateBottomViewStatus();

    /**
     * 更新侧栏的显示
     *
     * @return
     */
    public abstract boolean updateLeftViewStatus();


    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            int visibleHeight = r.height();
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight;
                return;
            }


        }
    };

    /**
     * 设置屏幕边距缩进
     */
    private void setScreenMargin() {
        rootView = (ViewGroup) findViewById(android.R.id.content);
        //屏幕缩进距离
        int screen_margin = (int) getResources().getDimension(R.dimen.screen_margin);
        //顶部状态栏高度
        int state_bar_height = (int)getResources().getDimension(R.dimen.state_bar_height);
        //bottom栏高度，没有计算向上小箭头
        int bottom_height = (int)getResources().getDimension(R.dimen.bottom_part_height);

        int margin_top = screen_margin + state_bar_height;
        int margin_bottom = screen_margin + bottom_height;
        rootView.setPadding(screen_margin, margin_top, screen_margin, margin_bottom);
    }
}
