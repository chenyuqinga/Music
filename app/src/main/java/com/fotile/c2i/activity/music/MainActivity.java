package com.fotile.c2i.activity.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotile.c2i.activity.music.base.BaseActivity;
import com.fotile.c2i.activity.music.util.AppManagerUtil;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;


import java.io.FileWriter;

import butterknife.BindView;

/**
 * 文件名称：MainActivity
 * 创建时间：2017/8/7 13:46
 * 文件作者：yaohx
 * 功能描述：主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{


    /***************************************Home**************************************/
    private static final String TAG = "MainActivity";
    /**
     * 喜马拉雅接口签名
     */
    public static final String appSecret = "7569aa71f10d4272fb2b40a0a1587200";
    /**
     * 主界面音乐名称
     */
    TextView tvHomeMusicName;
    /**
     * 主界面音乐暂停按钮
     */
    ImageView imgBtnHomeMusic;
    /**
     * 主界面音乐列表按钮
     */
    ImageView imgHomeMusicList;
    /**
     * music在线音乐标题
     */
    TextView tvHomeMusicTitle;
    /**
     * music布局
     */
    RelativeLayout rLayoutM;
    /**
     * 喜马拉雅控件
     */
    private XmPlayerManager xmPlayerManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //放在这里是有原因的，不要放在super.onCreate后面
        initApp();
        super.onCreate(savedInstanceState);
        rLayoutM=(RelativeLayout)findViewById(R.id.rLayout_home_music);
        tvHomeMusicName=(TextView)findViewById(R.id.tv_home_music_name);
        imgHomeMusicList=(ImageView)findViewById(R.id.img_home_music_list);
        tvHomeMusicTitle=(TextView)findViewById(R.id.tv_home_music_title);
        initView();
        initXm();
        //电源键返回键
        cmdFileWriter("/proc/power_key", "158");

    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initView() {
        rLayoutM.setOnClickListener(this);
        imgBtnHomeMusic.setOnClickListener(this);
        imgHomeMusicList.setOnClickListener(this);


    }

    /**
     * 初始化喜马拉雅
     */
    private void initXm() {
        xmPlayerManager = XmPlayerManager.getInstance(MainActivity.this);
        xmPlayerManager.init();
        xmPlayerManager.addPlayerStatusListener(playerStatusListener);
        xmPlayerManager.setBreakpointResume(false);
    }


    public void onClick(View v) {
        int id=v.getId() ;

            //在线音乐
           if(id==R.id.rLayout_home_music) {
               if (imgBtnHomeMusic.getVisibility() == View.VISIBLE) {
                   startMusicPlayActivity();
               } else {
                   launchActivity(MusicOnlineActivity.class);
               }

           }

            //在线音乐
           else if(id== R.id.imgbtn_home_music) {
               if (xmPlayerManager.isPlaying()) {
                   xmPlayerManager.pause();
                   imgBtnHomeMusic.setImageResource(R.mipmap.btn_home_music_play);
               } else {
                   xmPlayerManager.play();
                   imgBtnHomeMusic.setImageResource(R.mipmap.btn_home_music_pause);
               }
           }
            //在线音乐
           else if(id==R.id.img_home_music_list) {
               startMusicTrackActivity();
           }


    }

    /**
     * 初始化activity中的变量
     */
    private void initApp() {

        //喜马拉雅接口签名
        CommonRequest.getInstanse().init(getApplication(), appSecret);
    }

    /**
     * 启动音乐播放界面
     */
    private void startMusicPlayActivity() {
        Intent intent = new Intent(this, MusicPlayActivity.class);
        intent.putExtra("isFromHome", true);
        startActivity(intent);
    }

    /**
     * 喜马拉雅内部类
     */
    private MusicPlayerStateListener playerStatusListener = new MusicPlayerStateListener() {
        /**
         *切歌
         * @param laModel 上一首model,可能为空
         * @param curModel 下一首model
         */
        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            String title = "";
            PlayableModel model = xmPlayerManager.getCurrSound();
            if (model != null) {
                String coverUrl = null;
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    coverUrl = info.getCoverUrlLarge();
                }
                tvHomeMusicName.setText(title);
                tvHomeMusicName.setSelected(true);
                //调用音乐界面图片
                // Glide.with(HomeScreenActivity.this).load(coverUrl).centerCrop().error(R.mipmap
                // .img_error_music).into(imgHomeMusic);
            }
            showMusicMainIcon();
        }


        //播放停止
        @Override
        public void onPlayStop() {
            hideMusicMainIcon();
        }

        //播放开始
        @Override
        public void onPlayStart() {

            imgBtnHomeMusic.setImageResource(R.mipmap.btn_home_music_pause);


        }

        /**
         * 播放进度回调
         * @param currPos
         * @param duration
         */
        @Override
        public void onPlayProgress(int currPos, int duration) {
        }

        //播放停止
        @Override
        public void onPlayPause() {
            imgBtnHomeMusic.setImageResource(R.mipmap.btn_home_music_play);
        }

        @Override
        public boolean onError(XmPlayerException e) {
            if (getCurrentActivityName(getApplicationContext()).contains("MainActivity")) {
                imgBtnHomeMusic.setImageResource(R.mipmap.btn_home_music_play);
            } else {
                hideMusicMainIcon();
            }

            return super.onError(e);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean updateBottomViewStatus() {

        return true;
    }

    @Override
    public boolean updateLeftViewStatus() {

        return true;
    }

    /**
     * 显示主界面的音乐按钮
     */
    private void showMusicMainIcon() {
        tvHomeMusicName.setVisibility(View.VISIBLE);
        //progressBarHomeMusic.setVisibility(View.VISIBLE);
        imgBtnHomeMusic.setVisibility(View.VISIBLE);
        imgHomeMusicList.setVisibility(View.VISIBLE);
        tvHomeMusicTitle.setVisibility(View.GONE);
    }


    /**
     * 隐藏主界面的音乐按钮
     */
    private void hideMusicMainIcon() {
        imgBtnHomeMusic.setVisibility(View.GONE);
        imgHomeMusicList.setVisibility(View.GONE);
        tvHomeMusicName.setVisibility(View.GONE);
        //progressBarHomeMusic.setVisibility(View.GONE);
        tvHomeMusicTitle.setVisibility(View.VISIBLE);
    }


    /**
     * 跳转至专辑列表页面
     */
    private void startMusicTrackActivity() {
        //获取当前音乐所属专辑并跳转至专辑列表页面
        PlayableModel model = xmPlayerManager.getCurrSound();
        String albumTitle = "";
        String albumId = "";
        if (model != null) {
            if (model instanceof Track) {
                Track info = (Track) model;
                SubordinatedAlbum album = info.getAlbum();
                if (album != null) {
                    albumId = albumId + album.getAlbumId();
                    albumTitle = album.getAlbumTitle();
                }
            }
            Intent intent = new Intent(this, MusicTrackActivity.class);
            intent.putExtra("title", albumTitle);
            intent.putExtra("albumId", albumId);
            intent.putExtra("isFromHome", true);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    public static String getCurrentActivityName(Context context) {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        String name = null == activity ? "" : activity.getClass().getSimpleName();
        return name;
    }

    /**
     * 写入控制命令的方法
     *
     * @param path /proc/power_key
     * @param str  disable:屏蔽电源键 enable:开启电源键
     * @return
     */
    static public boolean cmdFileWriter(String path, String str) {
        try {
            FileWriter fw = new FileWriter(path);
            fw.write(str, 0, str.length());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
