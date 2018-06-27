package com.fotile.c2i.activity.music.adapter;


import android.app.Fragment;
import android.app.FragmentManager;

import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;

import java.util.List;

/**
 * 文件名称：MusicAlbumViewpagerAdapter
 * 创建时间：17-9-13 下午1:59
 * 文件作者：zhangqiang
 * 功能描述：音乐专辑界面tablayout与viewpager的适配器
 */
public class MusicAlbumViewpagerAdapter extends CommonFragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<Attributes> attributesList;


    public MusicAlbumViewpagerAdapter(FragmentManager fm, List<Fragment> fragments, List<Attributes> attributesList) {
        super(fm);
        this.fragments = fragments;
        this.attributesList = attributesList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return attributesList.get(position).getDisplayName();
    }
}


