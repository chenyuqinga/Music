package com.fotile.c2i.activity.music;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.c2i.activity.music.adapter.MusicTrackRecyclerAdapter;
import com.fotile.c2i.activity.music.customview.RotationLoadingView;
import com.fotile.c2i.activity.music.model.view.MusicTrackView;
import com.fotile.c2i.activity.music.presenter.MusicTrackPresenter;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import butterknife.BindView;

import static com.fotile.c2i.activity.music.MusicAlbumFragment.ALBUMID;
import static com.fotile.c2i.activity.music.MusicAlbumFragment.TITLE;

/**
 * 文件名称：MusicAlbum
 * 创建时间：17-8-16 下午2:14
 * 文件作者：zhangqiang
 * 功能描述：显示音乐列表界面
 */
public class MusicTrackActivity extends BaseMusicActivity implements FavoriteItemClickListener.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MusicTrackActivity";

    /**
     * 更新音乐的消息类型
     */
    private static final int MESSAGE_UPDATE_MUSIC = 1;

    /**
     * 专辑名称
     */
    TextView tvCategoryTitle;

    /**
     * 提示区域
     */
    LinearLayout lLayoutContentArea;
    /**
     * 提示区域
     */
    LinearLayout lLayoutTipArea;
    /**
     * 加载中的提示图片
     */
    RotationLoadingView imgLoading;

    /**
     * 提示语信息
     */
    TextView tvTip;

    /**
     * 请求错误提示信息
     */
    TextView tvRequestMusic;
    /**
     * 连接网络按钮
     */
    TextView btnConnectNetwork;
    /**
     * 专辑下的音乐列表
     */
    RecyclerView recyclerViewMusicTrack;

    private int pageId = 1;
    private TrackList trackList;
    /**
     * 专辑ID
     */
    private String albumId;
    private MusicTrackPresenter musicTrackPresenter = new MusicTrackPresenter();
    /**
     * 是否是从主页跳转
     */
    private boolean isFromHome;
    /**
     * 音乐列表适配器
     */
    private MusicTrackRecyclerAdapter musicTrackRecyclerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_track;
    }

    @Override
    public boolean updateBottomViewStatus() {
//        BottomView.getInstance(this).setBottomVisiable(true);
//        BottomView.getInstance(this).setButtonVisible(true, true);
        return true;
    }

    @Override
    public boolean updateLeftViewStatus() {
//        LeftView.getInstance(this).setLeftViewVisiable(true);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvCategoryTitle=(TextView)findViewById(R.id.tv_category_title);
        lLayoutContentArea=(LinearLayout)findViewById(R.id.lLayout_music_track_content);
        lLayoutTipArea=(LinearLayout)findViewById(R.id.lLayout_music_track_tip_area) ;
        imgLoading=(RotationLoadingView)findViewById(R.id.img_music_track_loading) ;
        tvTip=(TextView)findViewById(R.id.tv_music_track_tip);
        tvRequestMusic=(TextView)findViewById(R.id.tv_music_track_request);
        btnConnectNetwork=(TextView)findViewById(R.id.tv_music_track_connect_network);
        recyclerViewMusicTrack= (RecyclerView)findViewById(R.id.recyclerView_track);
        initView();
        initData();
    }


    private void initData() {
        Intent intent = getIntent();
        String albumTitle = intent.getStringExtra(TITLE);
        albumId = intent.getStringExtra(ALBUMID);
        musicTrackPresenter.onCreate(compositeSubscription);
        musicTrackPresenter.attachView(musicTrackView);
        if (isNetworkAvailable(this)){
            musicTrackPresenter.getDataTrack(albumId, pageId);
            updateTip(true);
        }else {
            updateTip(false);
        }
        tvCategoryTitle.setText(albumTitle);
    }

    private void initView() {
        isFromHome = getIntent().getBooleanExtra("isFromHome", false);
        recyclerViewMusicTrack.addOnItemTouchListener(new FavoriteItemClickListener(this, this));
        OnTrackScrollListener listener = new OnTrackScrollListener();
        recyclerViewMusicTrack.addOnScrollListener(listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        recyclerViewMusicTrack.setLayoutManager(layoutManager);
        musicTrackRecyclerAdapter = new MusicTrackRecyclerAdapter(this);
        recyclerViewMusicTrack.setAdapter(musicTrackRecyclerAdapter);
        lLayoutContentArea.setVisibility(View.GONE);
        btnConnectNetwork.setOnClickListener(this);
        tvRequestMusic.setOnClickListener(this);
        registerWifiReceiver();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_music_track_connect_network)
        //                Intent intent = new Intent(this, SettingActivity.class);
        //                startActivity(intent);
        {
        } else if (id == R.id.tv_music_track_request) {
            musicTrackPresenter.getDataTrack(albumId, pageId);
            updateTip(true);
        }
    }

    /**
     * 滑到最右端
     */
    private class OnTrackScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int lastVisibleItem = manager.findFirstVisibleItemPosition();
            int totalItemCount = manager.getItemCount();

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (lastVisibleItem == (totalItemCount - 10)) {
                    musicTrackPresenter.getDataTrack(albumId, pageId);
                }

            }
        }
    }

    /**
     * 回调的专辑下的音乐列表
     */
    MusicTrackView musicTrackView = new MusicTrackView() {
        @Override
        public void onSuccess(TrackList list) {
            if (trackList == null) {
                trackList = list;
            } else {
                trackList.getTracks().addAll(list.getTracks());
            }
            pageId++;
            musicTrackRecyclerAdapter.updateList(trackList);
            hideTip();
            lLayoutContentArea.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(String errorMsg) {
            updateTip(false);
        }
    };

    /**
     * 搜索列表的item点击事件，并且跳转到播放界面
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        XmPlayerManager.getInstance(MusicTrackActivity.this).playList(trackList, position);
        launchActivity(MusicPlayActivity.class);
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromHome) {
            launchActivity(MusicOnlineActivity.class);
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(wifiReceiver);
        musicHandler.removeMessages(MESSAGE_UPDATE_MUSIC);
        super.onDestroy();
    }

    /**
     * 注册网络监听
     */
    private void registerWifiReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, intentFilter);
    }

    /**
     * 监听wifi是否链接上
     */

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != info && info.getState().equals(NetworkInfo.State.CONNECTED)){
                    musicHandler.removeMessages(MESSAGE_UPDATE_MUSIC);
                    musicHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_MUSIC, 1500);
                }
            }
        }
    };

    private Handler musicHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_UPDATE_MUSIC:
                    if (null == trackList){
                        musicTrackPresenter.getDataTrack(albumId, pageId);
                    }
                    break;
            }

        }
    };

    /**
     * 更新提示
     * @param hasData
     */
    private void updateTip(boolean hasData){
        if (null != trackList) return;
        lLayoutContentArea.setVisibility(View.GONE);
        if (hasData) {
            lLayoutTipArea.setVisibility(View.VISIBLE);
            imgLoading.setVisibility(View.VISIBLE);
            tvTip.setVisibility(View.VISIBLE);
            imgLoading.startRotationAnimation();
            tvTip.setText(getString(R.string.str_loading));
            btnConnectNetwork.setVisibility(View.GONE);
            tvRequestMusic.setVisibility(View.GONE);
        } else {
            if (!isNetworkAvailable(this)) {
                lLayoutTipArea.setVisibility(View.VISIBLE);
                imgLoading.setVisibility(View.GONE);
                imgLoading.stopRotationAnimation();
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getString(R.string.str_network_unavailable_tip));
                btnConnectNetwork.setVisibility(View.VISIBLE);
                tvRequestMusic.setVisibility(View.GONE);
            } else {
                lLayoutTipArea.setVisibility(View.VISIBLE);
                imgLoading.setVisibility(View.GONE);
                imgLoading.stopRotationAnimation();
                tvRequestMusic.setVisibility(View.VISIBLE);
                tvTip.setVisibility(View.GONE);
                btnConnectNetwork.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 隐藏提示
     */
    private void hideTip() {
        lLayoutTipArea.setVisibility(View.GONE);
        imgLoading.stopRotationAnimation();
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
