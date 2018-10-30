package com.fotile.c2i.activity.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.c2i.activity.music.adapter.MusicSearchRecyclerAdapter;
import com.fotile.c2i.activity.music.customview.AutoLinefeedLayout;
import com.fotile.c2i.activity.music.customview.RotationLoadingView;
import com.fotile.c2i.activity.music.model.view.MusicsSearchView;
import com.fotile.c2i.activity.music.presenter.MusicSearchPresenter;
import com.fotile.c2i.activity.music.util.LogUtil;
import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.ArrayList;



/**
 * 文件名称：MusicSearchActivity
 * 创建时间：17-8-17 下午2:59
 * 文件作者：zhangqiang
 * 功能描述：Music搜索界面
 */
public class MusicSearchActivity extends BaseMusicActivity implements View.OnClickListener, FavoriteItemClickListener
        .OnItemClickListener {

    private static final String TAG = "MusicSearchActivity";

    /**
     * 缓存最大的搜索历史关键词数量
     *
     */
    private static final int MAX_QUERY_KEY_STRING_COUNT = 5;

    /**
     * 搜索的历史记录保存的文件名
     */
    private static final String SHARE_PREFERENCES_NAME = "searchHistoryList";
    private static final String SHARE_PREFERENCES_KEY = "searchMusicList";

    /**
     * 搜索editText
     */
    EditText edSearch;
    /**
     * 搜索显示GridView
     */
    RecyclerView recyclerViewMusicSearch;
    /**
     * 搜索图标
     */
    ImageView imgSearchIcon;
    /**
     * 清除图标
     */
    ImageView imgCancelIcon;
    /**
     * 历史记录
     */
    LineWrapLayout lineWrapLayout;
    /**
     * 清除历史记录
     */
    ImageView imgDeleteHistory;
    /**
     * 显示历史记录的LinearLayout
     */
    LinearLayout lLayoutHistory;
    /**
     * 搜索菜谱结果为空提示布局
     */
    LinearLayout lLayoutSearchEmpty;
    /**
     * 搜索菜谱结果为空提示img
     */
    ImageView imgSearchEmpty;
    /**
     * 搜索菜谱结果为空提示TextView
     */
    TextView tvMusicSearchEmpty;
    /**
     * view
     */
    View view;
    /**
     * search column
     */
    FrameLayout search_column;
    private int pageId = 1;

    private ArrayList<String> historySearchList = new ArrayList<>();
    private SearchTrackList searchTrackList;
    private MusicSearchPresenter musicSearchPresenter = new MusicSearchPresenter();

    private MusicSearchRecyclerAdapter adapter;

    private String queryString;
    private Context context;
    /**
     * 输入法管理器
     */
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        edSearch = (EditText) findViewById(R.id.ed_search);
        recyclerViewMusicSearch = (RecyclerView) findViewById(R.id.recyclerView_search);
        imgSearchIcon = (ImageView) findViewById(R.id.img_search_icon);
        imgCancelIcon = (ImageView) findViewById(R.id.img_cancel_icon);
        //autoLinefeedLayout = (AutoLinefeedLayout) findViewById(R.id.history_search);
        lineWrapLayout=(LineWrapLayout)findViewById(R.id.history_search);
        lLayoutHistory = (LinearLayout) findViewById(R.id.lLayout_history);
        lLayoutSearchEmpty = (LinearLayout) findViewById(R.id.lLayout_search_empty);
        imgSearchEmpty=(ImageView)findViewById(R.id.img_search_empty);
        tvMusicSearchEmpty=(TextView)findViewById(R.id.tv_music_search_empty);
        imgDeleteHistory=(ImageView) findViewById(R.id.img_btn_deleteHistory);
        search_column=(FrameLayout)findViewById(R.id.search_column);
        view=(View)findViewById(R.id.view) ;
        initView();
        initData();
    }


    private void initData() {
        musicSearchPresenter.onCreate(compositeSubscription);
        musicSearchPresenter.attachView(musicSearchView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_search;
    }


    private void initView() {
        //控制软键盘
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        readSearchHistory();
        recyclerViewMusicSearch.addOnItemTouchListener(new FavoriteItemClickListener(this, this));
        imgCancelIcon.setOnClickListener(this);
        imgDeleteHistory.setOnClickListener(this);
        OnSearchEditorActionListener listener = new OnSearchEditorActionListener();
        edSearch.setOnEditorActionListener(listener);
        OnSearchScrollListener onSearchScrollListener = new OnSearchScrollListener();
        recyclerViewMusicSearch.addOnScrollListener(onSearchScrollListener);

        adapter = (MusicSearchRecyclerAdapter) recyclerViewMusicSearch.getAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMusicSearch.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        adapter = new MusicSearchRecyclerAdapter(this);
        recyclerViewMusicSearch.setAdapter(adapter);
        recyclerViewMusicSearch.setVisibility(View.GONE);
    }

    /**
     * 读取历史记录
     */
    private void readSearchHistory() {
        LogUtil.LOGE("---du","读取历史记录");
        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        String json = preferences.getString(SHARE_PREFERENCES_KEY, "[]");
        Gson gson = new Gson();
      //  autoLinefeedLayout.removeAllViews();
       lineWrapLayout.removeAllViews();
        historySearchList = (ArrayList<String>) gson.fromJson(json, ArrayList.class);

        for (String key : historySearchList) {
            final Button btn = new Button(this);
            btn.setText(key);
            btn.setAllCaps(false);
            btn.setTextSize(26);
            btn.setAlpha((float) 0.7);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edSearch.setText(btn.getText());
                    edSearch.setSelection(btn.getText().length());
                    if (null != searchTrackList) {
                        searchTrackList.getTracks().clear();
                    }
                    pageId = 1;
                    queryString = btn.getText().toString();
                    //请求数据
                    musicSearchPresenter.getMusicSearch(queryString, pageId);
                    hideKeyboard();
                    lLayoutHistory.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
            });
           // autoLinefeedLayout.addView(btn);
            lineWrapLayout.addView(btn);
        }
    }

    /**
     * 存储历史记录
     */
    private void saveSearchHistory() {
        Gson gson = new Gson();
        String json = gson.toJson(this.historySearchList);
        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SHARE_PREFERENCES_KEY, json);
        editor.commit();
    }

    /**
     * 滑到最右端刷新数据
     */
    private class OnSearchScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int lastVisibleItem = manager.findLastVisibleItemPosition();
            int totalItemCount = manager.getItemCount();
            int visibleItemCount = manager.getChildCount();
            if (visibleItemCount > 0 && lastVisibleItem == totalItemCount - 1 && newState == recyclerView
                    .SCROLL_STATE_IDLE) {
                if (pageId < searchTrackList.getTotalPage()) {
                    musicSearchPresenter.getMusicSearch(queryString, pageId);
                }
            }
        }
    }


    /**
     * 回调的搜索列表
     */
    MusicsSearchView musicSearchView = new MusicsSearchView() {

        @Override
        public void onSuccess(SearchTrackList list) {
            recyclerViewMusicSearch.setVisibility(View.VISIBLE);
            lLayoutSearchEmpty.setVisibility(View.GONE);
           // tvSearchEmptyTip.setVisibility(View.GONE);
            if (searchTrackList == null) {
                searchTrackList = list;
            } else {
                searchTrackList.getTracks().addAll(list.getTracks());
            }
            pageId++;
            LogUtil.LOGD(TAG, "onSuccess: pageId----" + pageId);
            adapter.setSearchTrackList(searchTrackList);
            adapter.notifyDataSetChanged();
            recyclerViewMusicSearch.scrollToPosition(0);
        }

        @Override
        public void onError(String error) {
            switch (error) {
                //搜索请求数据未搜索到
                case MusicSearchPresenter.DATA_NULL:
                case MusicSearchPresenter.ILLEGAL_ERROR:
                    tvMusicSearchEmpty.setText(getString(R.string.str_search_music_empty));
                    break;
                default:
                    //搜索请求数据网络异常
                    tvMusicSearchEmpty.setText(getString(R.string.network_error));
                    break;
            }
            lLayoutSearchEmpty.setVisibility(View.VISIBLE);
            tvMusicSearchEmpty.setVisibility(View.VISIBLE);
            imgSearchEmpty.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onClick(View v) {
        edSearch.setText("");
        backToHistory();
        inputMethodManager.showSoftInput(edSearch, 0);
    }

    /**
     * 搜索列表的item点击事件，并且播放音乐
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        XmPlayerManager.getInstance(MusicSearchActivity.this).playList(searchTrackList, position);
        launchActivity(MusicPlayActivity.class);
    }

    private class OnSearchEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            LogUtil.LOGD(TAG, "OnSearchEditorActionListener:onEditorAction");

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                queryString = edSearch.getText().toString().trim();
                if (TextUtils.isEmpty(queryString)) {
                    hideKeyboard();
                  /*  TopSnackBar.make(context, context.getString(R.string.search_empty_tip), TopSnackBar.LENGTH_SHORT)
                            .show();*/
                    return false;
                }

                if (!historySearchList.contains(queryString)) {
                    if (historySearchList.size() == MAX_QUERY_KEY_STRING_COUNT) {
                        historySearchList.remove(historySearchList.size() - 1);
                    }
                    historySearchList.add(0, queryString);
                } else {
                    historySearchList.remove(queryString);
                    historySearchList.add(0, queryString);
                }
                if (null != searchTrackList) {
                    searchTrackList.getTracks().clear();
                }
                pageId = 1;
                saveSearchHistory();
              //  imgCancelIcon.setVisibility(View.GONE);
                //请求数据
                musicSearchPresenter.getMusicSearch(queryString, pageId);

                hideKeyboard();
                lLayoutSearchEmpty.setVisibility(View.GONE);
               // tvSearchEmptyTip.setVisibility(View.GONE);
                lLayoutHistory.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                recyclerViewMusicSearch.setVisibility(View.GONE);

            }

            return true;
        }
    }

    @Override
    public boolean updateBottomViewStatus() {
       /* BottomView.getInstance(this).setBottomVisiable(true);
        BottomView.getInstance(this).setButtonVisible(true, true);*/
        return true;
    }

    @Override
    public boolean updateLeftViewStatus() {
       /* LeftView.getInstance(this).setLeftViewVisiable(true);*/
        return true;
    }

    @Override
    public void finish() {
        if (recyclerViewMusicSearch.getVisibility() == View.VISIBLE
                || lLayoutHistory.getVisibility() == View.VISIBLE) {
            backToHistory();
        } else {
            super.finish();
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (null != getCurrentFocus()) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 回到历史记录界面
     */
    private void backToHistory() {
        readSearchHistory();
        lLayoutHistory.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
     //   imgCancelIcon.setVisibility(View.VISIBLE);
        recyclerViewMusicSearch.setVisibility(View.GONE);
       // tvSearchEmptyTip.setVisibility(View.GONE);
        lLayoutSearchEmpty.setVisibility(View.GONE);
    }


}
