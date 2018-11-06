package com.fotile.c2i.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotile.c2i.activity.music.FavoriteItemClickListener;
import com.fotile.c2i.activity.music.R;
import com.fotile.c2i.activity.music.adapter.MusicAlbumRecyclerAdapter;
import com.fotile.c2i.activity.music.base.BaseFragment;
import com.fotile.c2i.activity.music.model.view.MusicOnlineView;
import com.fotile.c2i.activity.music.presenter.MusicOnlinePresenter;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 文件名称：MusicAlbumFragment
 * 创建时间：17-9-11 上午8:57
 * 文件作者：zhangqiang
 * 功能描述：专辑的fragment
 */
public  class MusicAlbumFragment extends BaseFragment implements FavoriteItemClickListener.OnItemClickListener {


    /**
     * 传值专辑名称的key
     */
    public static final String TITLE = "title";

    /**
     * 传值专辑名称的id
     */
    public static final String ALBUMID = "albumId";
    /**
     * 音乐专辑recyclerview
     */

    RecyclerView recyclerViewMusicAlbum;

    /**
     * 音乐请求回来失败
     */
    TextView tvTipError;
    /*
     */
    ImageView musicCover;
    /**
     * 取不同元数据属性的attr_key和atrr_value组成任意个数的key-value键值
     */
    private String metadataAttributes;


    private MusicAlbumRecyclerAdapter adapter;

    /**
     * 断网
     */
    ImageView imgInternetOff;
    /**
     * 连接网络按钮
     */
    TextView tvConnectNetwork;
    /**
     * 请求错误重连
     */
    TextView tvRequestMusic;
    /**
     * 一次请求数据二十个
     */
    private int pageId = 1;

    private AlbumList albumList;

    private MusicOnlinePresenter musicOnlinePresenter = new MusicOnlinePresenter();

    public MusicAlbumFragment() {

    }

    public MusicAlbumFragment(String metadataAttributes) {
        this.metadataAttributes = metadataAttributes;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        pageId = 1;

        recyclerViewMusicAlbum = (RecyclerView)view.findViewById(R.id.recyclerView_music_album);
        tvTipError=(TextView)view.findViewById(R.id.tv_tip_music_request);
        musicCover = (ImageView) view.findViewById(R.id.img_music);
        imgInternetOff=(ImageView)view.findViewById(R.id.img_internet_off);
        tvRequestMusic=(TextView)view.findViewById(R.id.tv_music_request) ;
        tvConnectNetwork=(TextView)view.findViewById(R.id.tv_music_connect_network) ;
        initView();
        setValues();
        initData();

        return view;
    }

    private void initView() {
        recyclerViewMusicAlbum.addOnItemTouchListener(new FavoriteItemClickListener(context, this));
        OnAlbumScrollListener listener = new OnAlbumScrollListener();
        recyclerViewMusicAlbum.addOnScrollListener(listener);

    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_music_album;
    }


    /**
     * 滑到最右端刷新数据
     */
    private class OnAlbumScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int lastVisibleItem = manager.findLastVisibleItemPosition();
            int totalItemCount = manager.getItemCount();
            int visibleItemCount = manager.getChildCount();

            if (visibleItemCount > 0 && lastVisibleItem == totalItemCount - 1 && newState == recyclerView.SCROLL_STATE_IDLE) {
                if (pageId < albumList.getTotalPage()) {
                    musicOnlinePresenter.getMetadataAlbumList(metadataAttributes, pageId);
                    MusicOnlineActivity parent = (MusicOnlineActivity) getActivity();
                    if (null != parent) {
                        parent.setSlide(false);
                    }
                }

            }
        }

    }


    /**
     * 请求数据
     */
    private void initData() {
        albumList = null;
        musicOnlinePresenter.attachView(musicOnlineView);
        musicOnlinePresenter.getMetadataAlbumList(metadataAttributes, pageId);
        if(!isNetworkAvailable(context)){
            tvTipError.setVisibility(View.VISIBLE);
            tvTipError.setText(R.string.str_network_unavailable_tip);
            imgInternetOff.setVisibility(View.VISIBLE);
            tvRequestMusic.setVisibility(View.GONE);
            tvConnectNetwork.setVisibility(View.VISIBLE);
            recyclerViewMusicAlbum.setVisibility(View.GONE);
        }
    }


    /**
     * recyclerView 适配数据
     */
    private void setValues() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewMusicAlbum.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        adapter = new MusicAlbumRecyclerAdapter(getActivity());
        recyclerViewMusicAlbum.setAdapter(adapter);

    }

    /**
     * 请求得到的音乐专辑数据
     */
    MusicOnlineView musicOnlineView = new MusicOnlineView() {
        @Override
        public void onGetMetadataAlbumListSuccess(AlbumList list) {
            hideTip();
            if (albumList == null) {
                albumList = list;
            } else {
                albumList.getAlbums().addAll(list.getAlbums());
            }
            pageId++;
            MusicOnlineActivity parent = (MusicOnlineActivity) getActivity();
            if (null != parent) {
                parent.setSlide(true);
            }
            adapter.setAlbumTrackList(albumList);
            adapter.notifyDataSetChanged();
            if (albumList != null && (albumList.getAlbums() == null || albumList.getAlbums().size() < 1)) {
                showTip();
            }
        }

        @Override
        public void onGetMetadataAlbumListError(String errorMsg) {
            showTip();
        }

        @Override
        public void onGetMetadataListSuccess(List<Attributes> list) {

        }

        @Override
        public void onGetMetadataListError(String errorMsg) {

        }
    };


    /**
     * 跳转专辑相应的音乐界面
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent(getActivity(), MusicTrackActivity.class);
        intent.putExtra(TITLE, albumList.getAlbums().get(position).getAlbumTitle());
        intent.putExtra(ALBUMID, "" + albumList.getAlbums().get(position).getId());
        ((MusicOnlineActivity) getActivity()).launchActivity(MusicTrackActivity.class, intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);


        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void showTip() {
        tvTipError.setVisibility(View.VISIBLE);
        tvTipError.setText(R.string.str_network_disabled);
        imgInternetOff.setVisibility(View.VISIBLE);
        tvRequestMusic.setVisibility(View.VISIBLE);
        tvConnectNetwork.setVisibility(View.GONE);
        recyclerViewMusicAlbum.setVisibility(View.GONE);
    }

    private void hideTip() {
        tvTipError.setVisibility(View.GONE);
        imgInternetOff.setVisibility(View.GONE);
        tvRequestMusic.setVisibility(View.GONE);
        recyclerViewMusicAlbum.setVisibility(View.VISIBLE);
    }
    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            return true;
        }
        return false;
    }
}

