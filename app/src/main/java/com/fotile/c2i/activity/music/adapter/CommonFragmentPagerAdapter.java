package com.fotile.c2i.activity.music.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 文件名称：CommonFragmentPagerAdapter
 * 创建时间：2017-09-13 14:33
 * 文件作者：shihuijuan
 * 功能描述：通用Fragment页面适配器(由于ActivityGroup中无法获取v4包的FragmentManager,
 * 所以这里将所有Fragment相关的类都改为非v4包)
 */

public abstract class CommonFragmentPagerAdapter extends PagerAdapter {

    private FragmentManager fragmentManager;
    private FragmentTransaction curTransaction = null;
    /**
     * 当前Fragment项
     */
    private Fragment currentPrimaryItem = null;

    public CommonFragmentPagerAdapter(FragmentManager fm) {
        fragmentManager = fm;
    }

    /**
     * 返回position位置的Fragment
     * @param position
     * @return
     */
    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (curTransaction == null) {
            curTransaction = fragmentManager.beginTransaction();
        }

        final long itemId = getItemId(position);

        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            curTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            curTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), itemId));
        }
        if (fragment != currentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (curTransaction == null) {
            curTransaction = fragmentManager.beginTransaction();
        }
        curTransaction.detach((Fragment)object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.setMenuVisibility(false);
                currentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            currentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (curTransaction != null) {
            curTransaction.commitAllowingStateLoss();
            curTransaction = null;
            fragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    /**
     * 获取item唯一标识
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * 生成Fragment名字
     * @param viewId
     * @param id
     * @return
     */
    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
