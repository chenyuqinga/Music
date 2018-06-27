package com.fotile.c2i.activity.music;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fotile.c2i.activity.music.util.LogUtil;
import com.fotile.c2i.activity.music.view.NetworkPopupWindow;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * 文件名称：MuiscPlayActivity
 * 创建时间：17-8-18 上午10:10
 * 文件作者：zhangqiang
 * 功能描述：音乐播放界面
 */
public class MusicPlayActivity extends BaseMusicActivity implements View.OnClickListener {


    private static final String TAG = "MusicPlayActivity";

    /**
     * 播放时音乐名称
     */
    @BindView(R.id.tv_play_name)
    TextView tvPlayName;

    @BindView(R.id.tv_play_author)
    TextView tvPlayAuthor;

    /**
     * * 播放背景
     */
    @BindView(R.id.img_play_background)
    ImageView imgPlayBackground;

    /**
     * 播放
     */
    @BindView(R.id.imgBtn_play)
    ImageView imgBtnPlay;

    /**
     * 下一首
     */
    @BindView(R.id.imgBtn_next)
    ImageView imgBtnNext;

    /**
     * 上一首
     */
    @BindView(R.id.imgBtn_pre)
    ImageView imgBtnPre;

    /**
     * 播放进度时长
     */
    @BindView(R.id.tv_playTime)
    TextView tvPlayTime;


    /**
     * 播放进度SeekBar
     */
    @BindView(R.id.sBar_progress)
    SeekBar seekBarProgress;

    /**
     * 音量seekbar
     */
    @BindView(R.id.sBar_sound)
    SeekBar seekBarSound;

    /**
     * 播放时音乐图像Img
     */
    @BindView(R.id.cImg_play_music)
    ImageView imgPlayMusic;

    /**
     * 音量manager
     */
    private AudioManager audioManager;

    /**
     * 喜马拉雅播放器
     */
    private XmPlayerManager xmPlayerManager;

    /**
     * 喜马拉雅
     */
    private CommonRequest xmCommonRequest;

    /**
     * 播放界面图片旋转动画
     */
    private Animation animationImage;

    /**
     * 更新播放进度
     */
    private boolean updateProgress = true;
    private Context context;

    /**
     * 是否是从主页跳转
     */
    private boolean isFromHome;

    /**
     * 网络异常提示
     */
    private NetworkPopupWindow networkTipWindow;
    private static long last_time = 0;
    private static final int ONE_HOUR = 60 * 60 * 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initView();
        initXm();
        initAnimation();
        if (isFromHome) {
            initMusicInfo();
        }
    }


    /**
     * 初始化动画
     */
    private void initAnimation() {

        animationImage = AnimationUtils.loadAnimation(context, R.anim.music_img_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();
        animationImage.setInterpolator(interpolator);
    }

    /**
     * 初始化喜马拉雅播放器
     */
    private void initXm() {

        xmCommonRequest = CommonRequest.getInstanse();
        xmPlayerManager = XmPlayerManager.getInstance(context);
        xmPlayerManager.setBreakpointResume(false);
        updatePreButton();
        xmPlayerManager.addPlayerStatusListener(playerStatusListener);
        xmPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                xmPlayerManager.removeOnConnectedListerner(this);

                xmCommonRequest.setDefaultPagesize(100);
                xmPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                LogUtil.LOGD(TAG, "播放器初始化成功");
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_play;
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

    private void initView() {
        isFromHome = getIntent().getBooleanExtra("isFromHome", false);
        imgBtnPlay.setOnClickListener(this);
        imgBtnNext.setOnClickListener(this);
        imgBtnPre.setOnClickListener(this);

        seekBarProgress.setOnSeekBarChangeListener(new SeekBarChangeListener());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBarSound.setMax(maxVolume);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarSound.setProgress(currentVolume);
        seekBarSound.setOnSeekBarChangeListener(new SeekBarChangeSoundEvent());
        imgBtnPlay.setImageResource(R.mipmap.btn_music_play);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //音乐播放与暂停
            case R.id.imgBtn_play:
                if (xmPlayerManager.isPlaying()) {
                    xmPlayerManager.pause();
                    imgPlayMusic.clearAnimation();
                } else {
                    xmPlayerManager.play();
                    imgPlayMusic.startAnimation(animationImage);
                }
                break;
            //下一首
            case R.id.imgBtn_next:
                if (fastclick()) {
                    xmPlayerManager.playNext();
                }
                break;
            //上一首
            case R.id.imgBtn_pre:
                if (fastclick()) {
                    xmPlayerManager.playPre();
                    xmCommonRequest.setDefaultPagesize(100);
                }
                break;
            default:
                break;
        }
//        ScreenTool.getInstance().addResetData("音乐播放界面点击");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (xmPlayerManager.isPlaying()) {
            imgPlayMusic.startAnimation(animationImage);
        }

    }


    /**
     * 实现监听声音SeekBar的类
     */
    private class SeekBarChangeSoundEvent implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            seekBarSound.setProgress(currentVolume);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    /**
     * 实现监听播放进度SeekBar的类
     */
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            xmPlayerManager.seekToByPercent(seekBar.getProgress() / (float) seekBar.getMax());
            updateProgress = true;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            updateProgress = false;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

    }


    @Override
    protected void onDestroy() {
        if (xmPlayerManager != null) {
            xmPlayerManager.removePlayerStatusListener(playerStatusListener);
        }
        if (null != networkTipWindow && networkTipWindow.isShowing()) {
            networkTipWindow.dismiss();
        }
//        if (Tool.isAwaitView() || ScreenSaverService.getCurrentState() == ScreenSaverService.SCREEN_SLEEP) {
//            ((BaseApplication) getApplication()).setCanPlayMusicScreen(true);
//        }

        super.onDestroy();
    }

    private IXmPlayerStatusListener playerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            LogUtil.LOGD(TAG, "onSoundPrepared");
            seekBarProgress.setEnabled(true);

        }

        /**
         *切歌
         * @param laModel 上一首model,可能为空
         * @param curModel 下一首model
         */
        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            LogUtil.LOGD(TAG, "onSoundSwitch index:" + curModel.toString());
            updatePreButton();
            PlayableModel model = xmPlayerManager.getCurrSound();
            if (model != null) {
                String title = null;
                String name = null;
                String coverUrl = null;
                int time =0;
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    name = info.getAnnouncer().getNickname();
                    coverUrl = info.getCoverUrlLarge();
                    time = info.getDuration();
                }
                tvPlayName.setText(title);
               //切换歌曲时，时间改问题，默认时间00
                tvPlayTime.setText(formatMusicTime(0) + " / " + formatMusicTime(time*1000));
                if (name != null) {
                    tvPlayAuthor.setText(name);
                } else {
                    tvPlayAuthor.setText(getResources().getString(R.string.unknown_author));
                }
                Glide.with(context).load(coverUrl).
                        bitmapTransform(new CropCircleTransformation(context)).
                        animate(animationImage).into(imgPlayMusic);

                Glide.with(context).load(coverUrl).
                        bitmapTransform(new BlurTransformation(context, 20)).into(imgPlayBackground);

            }
            updateButtonStatus();
        }

        /**
         * 播放停止
         */
        @Override
        public void onPlayStop() {
            LogUtil.LOGD(TAG, "onPlayStop");
            imgBtnPlay.setImageResource(R.mipmap.btn_music_play);
            imgPlayMusic.startAnimation(animationImage);
        }

        /**
         * 播放开始
         */
        @Override
        public void onPlayStart() {
            LogUtil.LOGE(TAG, "onPlayStart");
            imgBtnPlay.setImageResource(R.mipmap.btn_music_pause);
            imgPlayMusic.startAnimation(animationImage);
            //音乐播放开始，重置屏保
//            ScreenTool.getInstance().addResetData("音乐播放开始");
        }


        /**
         * 播放进度回调
         * @param currPos
         * @param duration
         */
        @Override
        public void onPlayProgress(int currPos, int duration) {
            String title = "";
            String coverUrl = "";
            String name = "";
            PlayableModel info = xmPlayerManager.getCurrSound();
            if (info != null) {
                if (info instanceof Track) {
                    title = ((Track) info).getTrackTitle();
                    name = ((Track) info).getAnnouncer().getNickname();
                    coverUrl = ((Track) info).getCoverUrlLarge();
                }
            }
            tvPlayName.setText(title);
            if (name != null) {
                tvPlayAuthor.setText(name);
            } else {
                tvPlayAuthor.setText(getResources().getString(R.string.unknown_author));
            }
            tvPlayTime.setText(formatMusicTime(currPos) + " / " + formatMusicTime(duration));
            Glide.with(context).load(coverUrl).bitmapTransform(new CropCircleTransformation(context)).into
                    (imgPlayMusic);
            Glide.with(context).load(coverUrl).
                    bitmapTransform(new BlurTransformation(context, 23)).into(imgPlayBackground);


            if (updateProgress && duration != 0) {
                seekBarProgress.setProgress((int) (100 * currPos / (float) duration));
            }
        }

        /**
         * 播放停止
         */
        @Override
        public void onPlayPause() {
            LogUtil.LOGD(TAG, "onPlayPause");
            imgPlayMusic.clearAnimation();
            imgBtnPlay.setImageResource(R.mipmap.btn_music_play);
        }

        /**
         * 播放停止
         */
        @Override
        public void onSoundPlayComplete() {
            LogUtil.LOGD(TAG, "onSoundPlayComplete");
            imgBtnPlay.setImageResource(R.mipmap.btn_music_play);
        }

        /**
         * 播放错误
         */
        @Override
        public boolean onError(XmPlayerException exception) {
            LogUtil.LOGD(TAG, "onError " + exception.getMessage());
            imgBtnPlay.setImageResource(R.mipmap.btn_music_play);
            if (!isNetworkAvailable(context)) {
                //TopSnackBar.make(context.getApplicationContext(), context.getString(R.string.str_network_interrupt)
                // , TopSnackBar.LENGTH_LONG).show();
                if (null == networkTipWindow) {
                    networkTipWindow = new NetworkPopupWindow(context);
                    networkTipWindow.setWindowSize(screen_width - leftWidth, screen_height - bottomHeight);
                    networkTipWindow.setBackgroundDrawable(null);
                }
                if (null != networkTipWindow && rootView.isAttachedToWindow() && !networkTipWindow.isShowing()) {
                    networkTipWindow.showAtLocation(rootView, Gravity.TOP, leftWidth, isDockTop ? bottomHeight : 0);
                }

            }
            return false;
        }

        /**
         * 缓冲进度回调
         */
        @Override
        public void onBufferProgress(int position) {
            seekBarProgress.setSecondaryProgress(position);
        }

        /**
         * 开始缓冲
         */
        public void onBufferingStart() {
            seekBarProgress.setEnabled(false);

        }

        /**
         * 结束缓冲
         */
        public void onBufferingStop() {
            seekBarProgress.setEnabled(true);
        }

    };

    /**
     * 更新上下按钮的状态
     */
    private void updateButtonStatus() {
        imgBtnPre.setEnabled(xmPlayerManager.hasPreSound());
        imgBtnNext.setEnabled(xmPlayerManager.hasNextSound());
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromHome) {
            startMusicTrackActivity();
        }
        xmPlayerManager.stop();

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

    private void initMusicInfo() {
        xmPlayerManager.seekTo(xmPlayerManager.getPlayCurrPositon());
        imgBtnPlay.setImageResource(xmPlayerManager.isPlaying() ? R.mipmap.btn_music_pause : R.mipmap.btn_music_play);
        PlayableModel model = xmPlayerManager.getCurrSound();
        if (model != null) {
            String title = null;
            String name = null;
            String coverUrl = null;
            if (model instanceof Track) {
                Track info = (Track) model;
                title = info.getTrackTitle();
                name = info.getAnnouncer().getNickname();
                coverUrl = info.getCoverUrlLarge();
            }
            tvPlayName.setText(title);
            if (name != null) {
                tvPlayAuthor.setText(name);
            } else {
                tvPlayAuthor.setText(getResources().getString(R.string.unknown_author));
            }
            Glide.with(context).load(coverUrl).
                    bitmapTransform(new CropCircleTransformation(context)).
                    animate(animationImage).into(imgPlayMusic);

            Glide.with(context).load(coverUrl).
                    bitmapTransform(new BlurTransformation(context, 20)).into(imgPlayBackground);

        }
    }

    /**
     * 设置前一首按钮是否可点击
     */
    private void updatePreButton() {
        if (xmPlayerManager.hasPreSound()) {
            imgBtnPre.setEnabled(true);
            imgBtnPre.setImageResource(R.mipmap.btn_music_prev);
        } else {
            imgBtnPre.setEnabled(false);
            imgBtnPre.setImageResource(R.mipmap.btn_music_pre_grey);
        }
    }
    /**
     * 限制按钮的快速点击情况
     *
     * @return true 执行click事件
     */
    public static boolean fastclick() {
        long time = System.currentTimeMillis();
        long interval = time - last_time;
        last_time = time;
        if (interval > 400) {
            return true;
        }
        return false;
    }

    /**
     * HH:mm:ss
     */
    public static String formatMusicTime(long ms) {

        SimpleDateFormat format = null;
        if (ms < ONE_HOUR) {
            format = new SimpleDateFormat("mm:ss");
        } else {
            format = new SimpleDateFormat("HH:mm:ss");
        }
        Date date = new Date(ms);
        return format.format(date);
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

