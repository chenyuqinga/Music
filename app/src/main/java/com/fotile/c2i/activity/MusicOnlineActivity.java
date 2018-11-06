package com.fotile.c2i.activity;

import android.app.Fragment;
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
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotile.c2i.activity.music.FavoriteItemClickListener;
import com.fotile.c2i.activity.music.R;
import com.fotile.c2i.activity.music.adapter.MusicAlbumRecyclerAdapter;
import com.fotile.c2i.activity.music.adapter.MusicAlbumViewpagerAdapter;
import com.fotile.c2i.activity.music.base.BaseFragment;
import com.fotile.c2i.activity.music.model.view.MusicOnlineView;
import com.fotile.c2i.activity.music.presenter.MusicOnlinePresenter;
import com.fotile.c2i.activity.music.util.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * 文件名称：MusicOnlineActivity
 * 创建时间：17-8-17 上午10:12
 * 文件作者：zhangqiang
 * 功能描述: music显示专辑及显示歌曲
 */

public class MusicOnlineActivity extends BaseMusicActivity implements View.OnClickListener {
    private static final String TAG = "MusicOnlineActivity";
    /**
     * 加载布局
     */
    LinearLayout lLayoutLoading;
    /**
     * 音乐Icon
     */
    ImageView icon_music;
    /**
     * 音乐文字
     */
    TextView text_music;

    /**
     * 更新音乐的消息类型
     */
    private static final int MESSAGE_UPDATE_MUSIC = 1;
    /**
     * 专辑分类tab按键
     */
    TabLayout tabLayoutCategory;
    /**
     * 专辑的viewpager
     */
    ViewPager viewPagerMusicContent;
    /**
     * 显示音乐专辑区域
     */
    LinearLayout currentActivityContent;
    /**
     * 搜索img
     */
    ImageView imgSearch;
    /**
     * 提示区域
     */
    LinearLayout lLayoutTipArea;
    /**
     * 断网
     */
    ImageView imgInternetOff;
    /**
     * Head Title
     */
    RelativeLayout headTitle;
    /**
     * 提示语信息
     */
    TextView tvTip;
    /**
     * 连接网络按钮
     */
    TextView TvConnectNetwork;
    /**
     * 请求错误重连
     */
    TextView tvRequestMusic;
    /**
     * tabview
     */
    TextView tabview;
    /**
     * 请求数据之后的回调
     */
    private MusicOnlinePresenter musicOnlinePresenter = new MusicOnlinePresenter();

    private boolean isSlide;

    /**
     * 音乐专辑界面tablayout与viewpager的适配器
     */
    private MusicAlbumViewpagerAdapter musicAlbumViewpagerAdapter;

    /**
     * 音乐类型
     */
    private List<Attributes> attributesList = new ArrayList<>();
    /**
     * 分类的数据的fragment
     */
    private List<Fragment> fragments = new ArrayList<>();


    @Override
    public int getLayoutId() {
        return R.layout.activity_music_online;
    }


    @Override
    public boolean updateBottomViewStatus() {
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
        LogUtil.LOGE("---Online", 2);
        tabLayoutCategory = (TabLayout) findViewById(R.id.tabs_category);
        viewPagerMusicContent = (ViewPager) findViewById(R.id.viewPager_music_content);
        currentActivityContent = (LinearLayout) findViewById(R.id.current_activity_content);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        lLayoutTipArea = (LinearLayout) findViewById(R.id.lLayout_music_tip_area);
        lLayoutLoading=(LinearLayout)findViewById(R.id.lLayout_loading);
        tvTip = (TextView) findViewById(R.id.tv_music_tip);
        imgInternetOff = (ImageView) findViewById(R.id.img_internet_off);
        TvConnectNetwork = (TextView) findViewById(R.id.tv_music_connect_network);
        icon_music = (ImageView) findViewById(R.id.icon_music);
        text_music = (TextView) findViewById(R.id.text_music);
        headTitle = (RelativeLayout) findViewById(R.id.head_title);
        tvRequestMusic = (TextView) findViewById(R.id.tv_music_request);
        tabview = (TextView) findViewById(R.id.tab_item_textview);
        initView();
        initData();

    }

    private void initData() {
        musicOnlinePresenter.onCreate(compositeSubscription);
        musicOnlinePresenter.attachView(musicOnlineView);
        if (isNetworkAvailable(this)) {
            musicOnlinePresenter.getMetadataList();
            updateTip(true);
        } else {
            updateTip(false);
        }

    }

    private void initView() {
        tabLayoutCategory.setTabMode(TabLayout.MODE_SCROLLABLE);
        imgSearch.setOnClickListener(this);
        TvConnectNetwork.setOnClickListener(this);
        //切换页面发现无网时显示提示信息
        viewPagerMusicContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!isNetworkAvailable(MusicOnlineActivity.this)) {
                    currentActivityContent.setVisibility(View.GONE);
                    attributesList.clear();
                    updateTip(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayoutCategory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LogUtil.LOGE("selected", 1);
                viewPagerMusicContent.setCurrentItem(tab.getPosition());
                View view = tab.getCustomView();
                if (null != view &&view instanceof TextView ) {
                    ((TextView) view).setTextSize(getResources().getDimension(R.dimen.tab_select));
                    ((TextView) view).setTextColor(getResources().getColor(R.color.select_txt));
                    reflex(tabLayoutCategory);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (null != view&&view instanceof TextView) {
                    ((TextView) view).setTextSize(getResources().getDimension(R.dimen.tab_unselect));
                    ((TextView) view).setTextColor(getResources().getColor(R.color.un_select_txt));
                    reflex(tabLayoutCategory);
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        reflex(tabLayoutCategory);
        registerWifiReceiver();

    }

    /**
     * 自定义Tab的View
     */
    private View getTabView(int currentPosition) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView textView = (TextView) view.findViewById(R.id.tab_item_textview);
        textView.setText(attributesList.get(currentPosition).getDisplayName());
        return view;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_search) {
            Intent musicIntent = new Intent(this, MusicSearchActivity.class);
            launchActivity(musicIntent);
        } else if (id == R.id.tv_music_connect_network) {
            //  如何解决module的activity跳到主项目的Activity
            //                Intent intent = new Intent(this, SettingActivity.class);
            //                startActivity(intent);
        } else if (id == R.id.tv_music_request) {
            musicOnlinePresenter.getMetadataList();
            updateTip(true);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(wifiReceiver);
        musicHandler.removeMessages(MESSAGE_UPDATE_MUSIC);
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                viewPagerMusicContent.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                viewPagerMusicContent.requestDisallowInterceptTouchEvent(false);
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 请求回调处理
     */
    MusicOnlineView musicOnlineView = new MusicOnlineView() {

        @Override
        public void onGetMetadataAlbumListSuccess(AlbumList albumList) {
        }

        @Override
        public void onGetMetadataAlbumListError(String errorMsg) {
            //Toast.makeText(getApplication(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onGetMetadataListSuccess(List<Attributes> list) {
            hideTip();
            //set音乐类型列表
            attributesList.clear();
            attributesList.addAll(list);
            currentActivityContent.setVisibility(View.VISIBLE);
            initFragment();
        }

        @Override
        public void onGetMetadataListError(String errorMsg) {
            updateTip(false);
            //Toast.makeText(getApplication(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
        }
    };

    private void initFragment() {
        fragments.clear();
        //请求成功后创建Fragment
        for (int i = 0; i < attributesList.size(); i++) {
            tabLayoutCategory.addTab(tabLayoutCategory.newTab().setText(attributesList.get(i).getDisplayName()));
            String metadataAttributes = attributesList.get(i).getAttrKey() + ":" + attributesList.get(i).getAttrValue();
            MusicAlbumFragment musicAlbumFragment = new MusicAlbumFragment(metadataAttributes);
            fragments.add(musicAlbumFragment);
        }

        musicAlbumViewpagerAdapter = new MusicAlbumViewpagerAdapter(getFragmentManager(), fragments, attributesList);
        viewPagerMusicContent.setAdapter(musicAlbumViewpagerAdapter);
        imgSearch.setVisibility(View.VISIBLE);
        LogUtil.LOGE("---哪吒2", 222);
        tabLayoutCategory.setupWithViewPager(viewPagerMusicContent);

        //设置tabview
        for (int i = 0; i < tabLayoutCategory.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayoutCategory.getTabAt(i);
            if (tab != null) {
                LogUtil.LOGE("SET",111);
                tab.setCustomView(getTabView(i));
            }
        }
        reflex(tabLayoutCategory);
    }



    /**
     * 控制viewpager是否可以滑动
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!isSlide) {
                    viewPagerMusicContent.requestDisallowInterceptTouchEvent(true);
                } else {
                    viewPagerMusicContent.requestDisallowInterceptTouchEvent(false);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isSlide) {
                    viewPagerMusicContent.requestDisallowInterceptTouchEvent(false);
                } else {
                    viewPagerMusicContent.requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setSlide(boolean isSlide) {
        this.isSlide = isSlide;
    }

    public void reflex(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                    mTabStripField.setAccessible(true);

                    LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);

                    int dp10 = dip2px(tabLayout.getContext(), 22);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //字多宽线就多宽，测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        LogUtil.LOGE("---width",width);
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                            LogUtil.LOGE("---widthget",width);
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        //乘以3解决点击变两行的问题
                        params.width = width*3;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static int dip2px(Context context, float dipValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 注册网络监听
     */
    private void registerWifiReceiver() {
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
                if (null != info && info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    musicHandler.removeMessages(MESSAGE_UPDATE_MUSIC);

                    musicHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_MUSIC, 1500);
                }
            }
        }
    };

    private Handler musicHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_UPDATE_MUSIC:
                    if (attributesList.size() == 0) {
                        musicOnlinePresenter.getMetadataList();
                        updateTip(true);
                    } else {
                        headTitle.setVisibility(View.VISIBLE);
                        imgSearch.setVisibility(View.VISIBLE);
                        lLayoutLoading.setVisibility(View.GONE);
                        LogUtil.LOGE("---哪吒1", 111);
                    }
                    break;
            }

        }
    };

    /**
     * 更新提示
     *
     * @param hasData
     */
    private void updateTip(boolean hasData) {
        if (attributesList.size() != 0)
            return;
        if (hasData) {
            lLayoutLoading.setVisibility(View.VISIBLE);
            lLayoutTipArea.setVisibility(View.GONE);
            headTitle.setVisibility(View.GONE);
            imgSearch.setVisibility(View.GONE);
        } else {
            //网络未连接
            if (!isNetworkAvailable(this)) {
                headTitle.setVisibility(View.VISIBLE);
                //   tabLayoutCategory.setVisibility(View.VISIBLE);
                lLayoutTipArea.setVisibility(View.VISIBLE);
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getString(R.string.str_network_unavailable_tip));
                TvConnectNetwork.setVisibility(View.VISIBLE);
                tvRequestMusic.setVisibility(View.GONE);
            } else {
                headTitle.setVisibility(View.VISIBLE);
                lLayoutTipArea.setVisibility(View.VISIBLE);
                TvConnectNetwork.setVisibility(View.GONE);
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getString(R.string.str_network_disabled));
                tvRequestMusic.setVisibility(View.VISIBLE);
                TvConnectNetwork.setVisibility(View.GONE);

            }
        }
    }

    /**
     * 隐藏提示
     */
    private void hideTip() {
        lLayoutTipArea.setVisibility(View.GONE);
    }
}
//
//    /**
//     * 判断网络是否可用
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context
//                .CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isAvailable()) {
//            return true;
//        }
//        return false;
//    }

