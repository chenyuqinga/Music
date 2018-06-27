package com.fotile.c2i.activity.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.fotile.c2i.activity.music.R;

/**
 * 文件名称：GoodProgressView
 * 创建时间：17-8-30 上午10:28
 * 文件作者：zhangqiang
 * 功能描述：主界面MusicProgressBar控件
 */
public class MusicProgressView extends View {

    /**
     * 默认样式
     */
    private int[] colorsArray = {getResources().getColor(R.color.music_progress_start), getResources().getColor(R.color.music_progress_end)};

    /**
     * 默认背景颜色
     */
    private int backgroundColor = getResources().getColor(R.color.music_progress_background);

    private Paint paint;

    /**
     * 进度值
     */
    private int progressValue = 0;
    private Handler handler;


    public MusicProgressView(Context context, AttributeSet attrs) {

        super(context, attrs);
        paint = new Paint();
        handler = new Handler();
        handler.post(runnable);
        progressValue = 0;
    }


    /**
     * 利用线程不断更新界面
     */
    private Runnable runnable = new Runnable() {
        public void run() {
            postInvalidate();
            handler.postDelayed(runnable, 50);
        }
    };


    public void setProgressValue(int progressValue) {
        if (progressValue > 100) {
            progressValue = 100;
        }
        this.progressValue = progressValue;
    }


    /**
     * 渐变色
     *
     * @param colors
     */
    public void setColors(int[] colors) {
        colorsArray = colors;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        float unit = Math.min(((float) mWidth) / 300, ((float) mHeight) / 30);
        float lineWidth = unit;
        float innerCircleDiameter = 12;
        float height = 30 * unit;
        float progressWidth = mWidth;
        float offsetHeight = height / 2;
        int count = colorsArray.length;
        int[] colors = new int[count];
        float section = ((float) progressValue) / 100;

        paint.setAntiAlias(true);
        paint.setStrokeWidth((float) lineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.TRANSPARENT);

        if (section > 1) {
            section = 1;
        }
        System.arraycopy(colorsArray, 0, colors, 0, count);
        paint.setShader(null);
        paint.setColor(backgroundColor);
        canvas.drawLine(section * progressWidth, offsetHeight, progressWidth, offsetHeight, paint);


        LinearGradient shader = new LinearGradient(0, 0, progressWidth, 0, colors, null,
                Shader.TileMode.CLAMP);
        paint.setShader(shader);

        canvas.drawLine(0, offsetHeight, section * progressWidth, offsetHeight, paint);

        paint.setShader(null);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(section * progressWidth, offsetHeight, innerCircleDiameter / 2, paint);
    }
}