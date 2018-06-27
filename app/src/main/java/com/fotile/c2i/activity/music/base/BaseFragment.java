package com.fotile.c2i.activity.music.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：BaseFragment
 * 创建时间：2017/8/7 13:40
 * 文件作者：yaohx
 * 功能描述：BaseFragment
 */
public abstract class BaseFragment extends Fragment {

    public BaseActivity context;

    protected View view;
    /**
     * 取自BaseActivity
     */
    public CompositeSubscription compositeSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initApp();
        return view;
    }

    private void initApp() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            this.context = (BaseActivity) activity;
            compositeSubscription = ((BaseActivity) activity).compositeSubscription;
        }
    }

    /**
     * 回收一些资源
     */
    public void recovery(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recovery();
    }

    public abstract int getLayoutId();

}
