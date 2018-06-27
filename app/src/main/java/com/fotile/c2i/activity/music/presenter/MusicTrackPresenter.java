package com.fotile.c2i.activity.music.presenter;

import com.fotile.c2i.activity.music.base.BasePresenter;
import com.fotile.c2i.activity.music.base.BaseView;
import com.fotile.c2i.activity.music.model.view.MusicTrackView;
import com.fotile.c2i.activity.music.util.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.Map;

import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：MusicTrackPresenter
 * 创建时间：17-8-18 下午6:26
 * 文件作者：zhangqiang
 * 功能描述：Music音乐列表的Presenter
 */
public class MusicTrackPresenter implements BasePresenter {

    private MusicTrackView musicTrackView;
    private static final String TAG = "MusicTrackPresenter";

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
    }

    @Override
    public void attachView(BaseView baseView) {
        musicTrackView = (MusicTrackView) baseView;
    }

    public void getDataTrack(String mAlbumId, int pageId) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, mAlbumId);
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, "" + pageId);

        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                musicTrackView.onSuccess(trackList);
            }

            @Override
            public void onError(int i, String s) {
                musicTrackView.onError(s);
                LogUtil.LOGD(TAG, "getDataTrack:onError: " + i + ", " + s);
            }
        });

    }
}
