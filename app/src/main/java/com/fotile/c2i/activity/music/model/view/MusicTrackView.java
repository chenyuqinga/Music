package com.fotile.c2i.activity.music.model.view;

import com.fotile.c2i.activity.music.base.BaseView;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

/**
 * 文件名称：MusicTrachView
 * 创建时间：17-8-18 下午6:28
 * 文件作者：zhangqiang
 * 功能描述：更新MusicsTrack的回調接口.
 */
public interface MusicTrackView extends BaseView {

    void onSuccess(TrackList list);
    void onError(String errorMsg);
}
