package com.fotile.c2i.activity.music.presenter;

import com.fotile.c2i.activity.music.base.BasePresenter;
import com.fotile.c2i.activity.music.base.BaseView;
import com.fotile.c2i.activity.music.model.view.MusicOnlineView;
import com.fotile.c2i.activity.music.util.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaData;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaDataList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：MusicOnlinePresenter
 * 创建时间：17-8-29 上午9:47
 * 文件作者：zhangqiang
 * 功能描述：Music专辑的Presenter
 */
public class MusicOnlinePresenter implements BasePresenter {

    private static final String TAG = "MusicOnlinePresenter";

    private static final String MUSIC_CATEGORY = "2";
    private static final String CALC_DIMENSION = "3";


    /**
     * 根据喜马拉雅所获的数据
     */
    private static final int MUSIC_TYPE = 1;

    private MusicOnlineView musicOnlineView;

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
    }

    @Override
    public void attachView(BaseView baseView) {
        musicOnlineView = (MusicOnlineView) baseView;
    }

    /**
     * 获取类型下的专辑列表
     * 属性键值组合下包含的热门专辑列表/最新专辑列表/最多播放专辑列表
     *
     * @param metadataAttributes 元数据属性列表
     */
    public void getMetadataAlbumList(String metadataAttributes, int pageId) {



        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.CATEGORY_ID, MUSIC_CATEGORY);
        param.put(DTransferConstants.CALC_DIMENSION, CALC_DIMENSION);
        param.put(DTransferConstants.METADATA_ATTRIBUTES, metadataAttributes);
        param.put(DTransferConstants.PAGE, "" + pageId);

        CommonRequest.getMetadataAlbumList(param, new IDataCallBack<AlbumList>() {

            @Override
            public void onSuccess(AlbumList albumList) {
                LogUtil.LOGD(TAG, "getMetadataAlbumList:onSuccess");
                musicOnlineView.onGetMetadataAlbumListSuccess(albumList);
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.LOGD(TAG, "getMetadataAlbumList:onError " + i + ", " + s);
                musicOnlineView.onGetMetadataAlbumListError("");
            }
        });
    }

    /**
     * 获取所有的音乐类型
     */
    public void getMetadataList() {

        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.CATEGORY_ID, MUSIC_CATEGORY);
        CommonRequest.getMetadataList(param, new IDataCallBack<MetaDataList>() {

            @Override
            public void onSuccess(MetaDataList metaDataList) {
                LogUtil.LOGD(TAG, "getMetadataList:onSuccess");
                List<MetaData> list = metaDataList.getMetaDatas();
                if (list != null && list.size() > 1) {
                    MetaData metaData = list.get(MUSIC_TYPE);//获取全部分类
                    List<Attributes> attributesList = metaData.getAttributes();

                    if (attributesList.size() > 0) {
                        musicOnlineView.onGetMetadataListSuccess(attributesList);
                    } else {
                        musicOnlineView.onGetMetadataListError("");
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.LOGD(TAG, "getMetadataList:onError " + i + ", " + s);
                musicOnlineView.onGetMetadataListError("");
            }
        });
    }
}
