package com.fotile.c2i.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.c2i.activity.music.FavoriteItemClickListener;
import com.fotile.c2i.activity.music.R;
import com.fotile.c2i.activity.music.adapter.MusicSearchRecyclerAdapter;
import com.fotile.c2i.activity.music.model.view.MusicsSearchView;
import com.fotile.c2i.activity.music.presenter.MusicSearchPresenter;
import com.fotile.c2i.activity.music.util.LogUtil;
import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;

import java.util.ArrayList;

/**
 * Created by chenyqi on 2018/11/5.
 */
//
//public class MusicSearchEmptyActivity extends BaseMusicActivity {
//    /**
//     * 清除图标
//     */
//    ImageView imgCancelIcon;
//    /**
//     * 搜索editText
//     */
//    EditText edSearch;
//    /**
//     * 搜索显示GridView
//     */
//    RecyclerView recyclerViewMusicSearch;
//    /**
//     * 输入法管理器
//     */
//    private InputMethodManager inputMethodManager;
//    FrameLayout search_column;
//    private int pageId = 1;
//
//    private ArrayList<String> historySearchList = new ArrayList<>();
//    private SearchTrackList searchTrackList;
//    private MusicSearchPresenter musicSearchPresenter = new MusicSearchPresenter();
//
//    private MusicSearchRecyclerAdapter adapter;
//
//    private String queryString;
//    private Context context;
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        search_column = (FrameLayout) findViewById(R.id.search_column);
//        edSearch = (EditText) findViewById(R.id.ed_search);
//        initView();
//        initData();
//    }
////    private void initView() {
////        //控制软键盘
////        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////        readSearchHistory();
////        MusicSearchEmptyActivity.OnSearchEditorActionListener listener = new MusicSearchEmptyActivity.OnSearchEditorActionListener();
////        edSearch.setOnEditorActionListener(listener);
////        adapter = (MusicSearchRecyclerAdapter) recyclerViewMusicSearch.getAdapter();
////    }
////    private void initData() {
////        musicSearchPresenter.onCreate(compositeSubscription);
////        musicSearchPresenter.attachView(musicSearchView);
////    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_search_empty;
//    }
//    /**
//     * 读取历史记录
//     */
//    private void readSearchHistory() {
//        LogUtil.LOGE("---du", "读取历史记录");
//        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
//        String json = preferences.getString(SHARE_PREFERENCES_KEY, "[]");
//        Gson gson = new Gson();
//        //  autoLinefeedLayout.removeAllViews();
//        lineWrapLayout.removeAllViews();
//        historySearchList = (ArrayList<String>) gson.fromJson(json, ArrayList.class);
//
//        for (String key : historySearchList) {
//            final Button btn = new Button(this);
//            btn.setText(key);
//            btn.setAllCaps(false);
//            btn.setTextSize(26);
//            btn.setAlpha((float) 0.7);
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    edSearch.setText(btn.getText());
//                    edSearch.setSelection(btn.getText().length());
//                    if (null != searchTrackList) {
//                        searchTrackList.getTracks().clear();
//                    }
//                    pageId = 1;
//                    queryString = btn.getText().toString();
//                    //请求数据
//                    musicSearchPresenter.getMusicSearch(queryString, pageId);
//                    hideKeyboard();
//                }
//            });
//        }
//    }
//
//    /**
//     * 清除历史记录
//     */
//    private void clearHistory() {
//        Gson gson = new Gson();
//        historySearchList.clear();
//        String json = gson.toJson(this.historySearchList);
//        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(SHARE_PREFERENCES_KEY, json);
//        editor.commit();
//    }
//
//    /**
//     * 存储历史记录
//     */
//    private void saveSearchHistory() {
//        Gson gson = new Gson();
//        String json = gson.toJson(this.historySearchList);
//        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(SHARE_PREFERENCES_KEY, json);
//        editor.commit();
//    }
//    /**
//     * 回调的搜索列表
//     */
//    MusicsSearchView musicSearchView = new MusicsSearchView() {
//
//        @Override
//        public void onSuccess(SearchTrackList list) {
//            recyclerViewMusicSearch.setVisibility(View.VISIBLE);
//            lLayoutSearchEmpty.setVisibility(View.GONE);
//            LogUtil.LOGE("1112",1112);
//            if (searchTrackList == null) {
//                searchTrackList = list;
//            } else {
//                searchTrackList.getTracks().addAll(list.getTracks());
//            }
//            pageId++;
//            LogUtil.LOGD(TAG, "onSuccess: pageId----" + pageId);
//            adapter.setSearchTrackList(searchTrackList);
//            adapter.notifyDataSetChanged();
//            recyclerViewMusicSearch.scrollToPosition(0);
//        }
//
//        @Override
//        public void onError(String error) {
//            switch (error) {
//                //搜索请求数据未搜索到
//                case MusicSearchPresenter.DATA_NULL:
//                case MusicSearchPresenter.ILLEGAL_ERROR:
//                    tvMusicSearchEmpty.setText(getString(R.string.str_search_music_empty));
//                    break;
//                default:
//                    //搜索请求数据网络异常
//                    tvMusicSearchEmpty.setText(getString(R.string.network_error));
//                    break;
//            }
//            lLayoutSearchEmpty.setVisibility(View.VISIBLE);
//        }
//    };
//    /**
//     * 隐藏软键盘
//     */
//    private void hideKeyboard() {
//        if (null != getCurrentFocus()) {
//            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
//                    InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }}