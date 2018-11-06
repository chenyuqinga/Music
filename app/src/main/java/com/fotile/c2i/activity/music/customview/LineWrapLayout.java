package com.fotile.c2i.activity.music.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fotile.c2i.activity.music.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称：LineWrapLayout
 * 创建时间：2018/10/29 12:49
 * 文件作者：chenyqi
 * 功能描述：自动换行
 */


public  class LineWrapLayout extends ViewGroup {
    private static final boolean DEBUG = true;
    private static final String TAG = "AutoLineFeedLayout";

    /**
     * 左间距
     */
    private int paddingLeft = 10;
    /**
     * 右间距
     */
    private int paddingRight = 10;
    /**
     *
     */
    private int paddingTop = 10;
    /**
     *
     */
    private int paddingBottom = 10;

    /**
     * 水平方向间距
     */
    private int horizontalSpace = 20;
    /**
     * 行间距
     */
    private int verticalSpace = 20;


    private List<Integer> listX;
    private List<Integer> listY;

    public LineWrapLayout(Context context) {
        super(context);

    }

    public LineWrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LineWrapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (DEBUG)
            Log.d(TAG, "--- onLayout changed :" + changed + " l :" + l + ",t :" + t + ",r :" + r + ",b :" + b);
        int count = getChildCount();
        int width = getWidth();
        Log.i(TAG, "宽度 :" + width);

        int startOffsetX = paddingLeft;// 横坐标开始
        int startOffsety = 0;//纵坐标开始
        int rowCount = 1;

        int preEndOffsetX = startOffsetX;

        for (int i = 0; i < count; i++) {
            final View childView = getChildAt(i);

            int w = childView.getMeasuredWidth();
            int h = childView.getMeasuredHeight();

            int x = listX.get(i);
            int y = listY.get(i);

            // 布局子控件
            childView.layout(x, y, x + w, y + h);
            childView.setBackgroundResource(R.drawable.editext_round);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (DEBUG)
            Log.v(TAG, "--- onMeasure()");

        int count = getChildCount();
        int width = measureWidth(widthMeasureSpec);
        Log.i(TAG, "宽度 :" + width);


        int startOffsetX = paddingLeft;// 横坐标开始
        int startOffsety = 0 + paddingTop;//纵坐标开始
        int rowCount = 1;

        int preEndOffsetX = startOffsetX;

        listX.clear();
        listY.clear();
        for (int i = 0; i < count; i++) {
            Log.v(TAG, "----");
            final View childView = getChildAt(i);
            // 设置子空间Child的宽高
            childView.measure(0, 0);
            // 获取子控件Child的宽高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            Log.v(TAG, "childWidth :" + childWidth + " childHeight :" + childHeight);
            preEndOffsetX = startOffsetX + childWidth /*+ CHILD_MARGIN*/;
            if (preEndOffsetX > width - paddingRight) {
                if (startOffsetX > paddingLeft) {
                    //换行
                    startOffsetX = paddingLeft;
                    startOffsety += childHeight + verticalSpace;
                    rowCount++;
                }
            }
            Log.d(TAG, "measure child :" + startOffsetX + ", " + startOffsety + ", " + preEndOffsetX + ", " + (startOffsety + childHeight));
            listX.add(startOffsetX);
            listY.add(startOffsety);

            //            childView.layout(startOffsetX, startOffsety, preEndOffsetX, startOffsety+childHeight);
            startOffsetX = startOffsetX + childWidth + horizontalSpace;
        }
        int lastLineHeight = 0;
        View lastChild = getChildAt(count - 1);
        if (null != lastChild) {
            lastLineHeight = lastChild.getMeasuredHeight();
        }
        setMeasuredDimension(measureWidth(widthMeasureSpec), startOffsety + lastLineHeight + paddingBottom);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // Default size if no limits are specified.
        int result = 400;

        if (specMode == MeasureSpec.AT_MOST) {
            // Calculate the ideal size of your control
            // within this maximum size.
            // If your control fills the available space
            // return the outer bound.
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            // If your control can fit within these bounds return that value.
            result = specSize;
        }
        return result;
    }

    private void init(AttributeSet attrs) {
        TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.AutoLineFeedLayout);
        int attrCount = attrArray.getIndexCount();
        for (int i = 0; i < attrCount; i++) {
            int attrId = attrArray.getIndex(i);
            if (attrId==R.styleable.AutoLineFeedLayout_horizontalSpacing) {
                    float dimen = attrArray.getDimension(attrId, 0);
                    horizontalSpace = (int) dimen;
                }
           else if(attrId==R.styleable.AutoLineFeedLayout_verticalSpacing) {
                    float dimen = attrArray.getDimension(attrId, 0);
                    verticalSpace = (int) dimen;
                }
            else if (attrId== R.styleable.AutoLineFeedLayout_paddingBottom){
                    float dimen = attrArray.getDimension(attrId, 0);
                    paddingBottom = (int) dimen;
                }
            else if(attrId==R.styleable.AutoLineFeedLayout_paddingLeft) {
                    float dimen = attrArray.getDimension(attrId, 0);
                    paddingLeft = (int) dimen;
                }
            else if (attrId==R.styleable.AutoLineFeedLayout_paddingRight){
                    float dimen = attrArray.getDimension(attrId, 0);
                    paddingRight = (int) dimen;
                }

            else if(attrId==R.styleable.AutoLineFeedLayout_paddingTop){
                    float dimen = attrArray.getDimension(attrId, 0);
                    paddingTop = (int) dimen;
                }
            else if(attrId== R.styleable.AutoLineFeedLayout_debug){

                }
        }

        listX = new ArrayList<Integer>();
        listY = new ArrayList<Integer>();
    }
}