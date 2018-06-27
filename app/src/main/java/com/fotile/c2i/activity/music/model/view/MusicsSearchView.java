package com.fotile.c2i.activity.music.model.view;

import com.fotile.c2i.activity.music.base.BaseView;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;

/**
 * 文件名称：MusicsSearchView
 * 创建时间：17-8-23 下午7:29
 * 文件作者：zhangqiang
 * 功能描述：更新MusicsSearch的回調接口.
 */
public interface MusicsSearchView extends BaseView {

    void onSuccess(SearchTrackList list);
    void onError(String musics);
}
