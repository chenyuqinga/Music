package com.fotile.c2i.activity.music.base;


import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：BasePresenter
 * 创建时间：2017/8/7 14:40
 * 文件作者：yaohx
 * 功能描述：MVP模式中的Presenter
 */
public interface BasePresenter {
    /**
     * 完成Presenter的一些初始化工作
     *
     * @param compositeSubscription
     */
    void onCreate(CompositeSubscription compositeSubscription);


    /**
     * 传递BaseView
     *
     * @param baseView
     */
    void attachView(BaseView baseView);
}
