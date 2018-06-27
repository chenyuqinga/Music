package com.fotile.c2i.activity.music.model.view;

import com.fotile.c2i.activity.music.base.BaseView;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;

import java.util.List;

/**
 * 文件名称：MusicOnlineView
 * 创建时间：17-8-29 上午9:33
 * 文件作者：zhangqiang
 * 功能描述：
 */
public interface MusicOnlineView extends BaseView {
    /**
     * 类型下的专辑列表
     *
     * @param albumList
     */
    void onGetMetadataAlbumListSuccess(AlbumList albumList);

    /**
     * 类型下的专辑列表
     *
     * @param errorMsg
     */
    void onGetMetadataAlbumListError(String errorMsg);

    /**
     * 获取全部类型
     *
     * @param list
     */
    void onGetMetadataListSuccess(List<Attributes> list);

    /**
     * 获取全部类型
     *
     * @param errorMsg
     */
    void onGetMetadataListError(String errorMsg);
}
