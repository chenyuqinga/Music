package com.fotile.c2i.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fotile.c2i.activity.music.R;
import com.fotile.c2i.activity.music.customview.ProgressTouchView;
import com.fotile.c2i.activity.music.util.LogUtil;
import com.fotile.c2i.activity.music.view.ExtraBoldTextView;

/**
 * 文件名称：VolumeDialog
 * 创建时间：2018/11/5 16:18
 * 文件作者：chenyqi
 * 功能描述：
 */

public class VolumeDialog extends Dialog {
    /**
     * 音量dialog布局
     */
    LinearLayout lLayoutVolume;
    /**
     * 加大音量
     */
    ImageView soundPlus;
    /**
     * 减小音量
     */
    ImageView soundMinus;
    /**
     * 音量显示
     */
    public ExtraBoldTextView imgVolume;
    /**
     * 音量自定义View
     */
    ProgressTouchView progressTouchView;
    /**
     * 音量manager
     */
    private AudioManager audioManager;
    int xDown;
    int yDown;
    int xUp;
    int yUp;

    private  Context context ;
    public VolumeDialog(Context context)
    {
        super(context,R.style.VolumeDialog);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    public void init(){
        LayoutInflater inflater=LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.dialog_sound_layout,null);
        setContentView(view);
        soundPlus = (ImageView) findViewById(R.id.img_sound_plus);
        soundMinus = (ImageView) findViewById(R.id.img_sound_minus);
        imgVolume = (ExtraBoldTextView) findViewById(R.id.img_volume);
        progressTouchView = (ProgressTouchView) findViewById(R.id.progress_touchView);
        lLayoutVolume=(LinearLayout)findViewById(R.id.lLayout_volume);
        lLayoutVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //消费掉整个布局的点击退出对话框事件
        soundMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        soundPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        progressTouchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imgVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initView() {
        float x;
        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //设置当前显示进度
        progressTouchView.setProgress((float)currentVolume/10+0.0f);
        imgVolume.setText(currentVolume * 10 + "");
        if(currentVolume * 10==0)
        {soundMinus.setImageResource(R.mipmap.img_sound_zero);}
        else{soundMinus.setImageResource(R.mipmap.img_sound_plus);}
        progressTouchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDown = (int) event.getX();
                        yDown = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xUp = (int) event.getX();
                        yUp = (int) event.getY();
                        int dx = xUp - xDown;
                        int dy = -(yUp - yDown);
                        int b = progressTouchView.getVolumeDisplay(dy);
                        int a = progressTouchView.setRealVolume(dy);
                        xDown = xUp;
                        yDown = yUp;
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, a, 0);
                        int Volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        LogUtil.LOGE("sound-currentRealVolume", Volume);
                        imgVolume.setText(b + "");
                        if(b==0)
                        {soundMinus.setImageResource(R.mipmap.img_sound_zero);}
                        else{soundMinus.setImageResource(R.mipmap.img_sound_plus);}
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void show() {
        super.show();
        //设置全屏
        WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
        layoutParams.alpha=0.85f;
        layoutParams.gravity= Gravity.BOTTOM;
        layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(0,0,0,0);
        getWindow().setWindowAnimations(R.style.dialog_anim_style);
        getWindow().setAttributes(layoutParams);
    }
}
