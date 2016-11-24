package com.ywp.circleprogressbarviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 16/11/15.
 */

public class CircleProgressBarView extends View {

    private int mColor; // 进度弧的颜色
    private float mLineWidth; // 设置的弧的宽度
    private int mDuration;  //动画时间
    private int setProgress; // xml文件中设置的进度值
    private int mProgress = 0; // 当前进度表上显示的进度值，默认为0
    private Boolean mUseAnim; // 是否使用动画
    private Paint mPaint = new Paint();
    private int mBackColor; //进度弧的背景颜色
    private float textSize; // 中间字体的大小
    private boolean isAniming = false; // 动画是否进行中
    private int mStyle; // 这是style，决定是空心还是实心圆,与STROKE、FILL结合使用
    public final int STROKE = 0;
    public final int FILL = 1;

    public CircleProgressBarView(Context context) {
        this(context, null);
    }

    public CircleProgressBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 分别拿到在xml中设置的值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBarView, defStyleAttr, 0);
        mColor = typedArray.getColor(R.styleable.CircleProgressBarView_color, Color.BLUE);
        mBackColor = typedArray.getColor(R.styleable.CircleProgressBarView_backColor, Color.GREEN);
        mLineWidth = typedArray.getDimension(R.styleable.CircleProgressBarView_lineWidth, 100);
        mDuration = typedArray.getInt(R.styleable.CircleProgressBarView_duration, 60);
        setProgress = typedArray.getInt(R.styleable.CircleProgressBarView_progress, 0);
        mUseAnim = typedArray.getBoolean(R.styleable.CircleProgressBarView_useAnim, false);
        textSize = typedArray.getDimension(R.styleable.CircleProgressBarView_textSize, 40);
        mStyle = typedArray.getInt(R.styleable.CircleProgressBarView_style, 0);
        typedArray.recycle();

        // 这里是当设置了进度不为0的时候要在进入的时候产生一个动画效果
        if (setProgress != 0) {
            setProgress(0, setProgress);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        throwException();
        switch (mStyle) {
            case STROKE: {
                mPaint.setStyle(Paint.Style.STROKE);
                break;
            }
            case FILL: {
                mPaint.setStyle(Paint.Style.FILL);
                break;
            }
        }

        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setColor(mBackColor);
        //  消除锯齿
        mPaint.setAntiAlias(true);
//        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mLineWidth / 2, mPaint);
        RectF ov = new RectF(mLineWidth / 2, mLineWidth / 2, getWidth() - mLineWidth / 2, getHeight() - mLineWidth / 2);
        canvas.drawArc(ov, -90, 360, true, mPaint);

        mPaint.setColor(mColor);

        RectF oval = new RectF(mLineWidth / 2, mLineWidth / 2, getWidth() - mLineWidth / 2, getHeight() - mLineWidth / 2);
        mPaint.setStrokeWidth(mLineWidth);

        // startAnim()方法配合此处实现动画的效果
        if (mStyle == FILL) {
            canvas.drawArc(oval, -90, mProgress * 360 / 100, true, mPaint);
        } else {
            canvas.drawArc(oval, -90, mProgress * 360 / 100, false, mPaint);
        }

        mPaint.setStrokeWidth(0);
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setTextSize(textSize);
        String text = "当前强化等级 ：" + mProgress;
        float textWidth = mPaint.measureText(text);
        canvas.drawText(text, getWidth() / 2 - textWidth / 2, getHeight() / 2 + textSize / 2, mPaint);

    }

    // 设置进度的方法
    public void setProgress(int start, int end) {
        if (mUseAnim) {
            // 使用动画效果刷新UI
            startAnim(start, end);
        } else {
            // 不使用动画效果刷新UI
            mProgress = end;
            invalidate();
        }

    }

    public void addTen() {
        if (10 + mProgress > 100) {
            Toast.makeText(getContext(), "强化值不能大于100", Toast.LENGTH_LONG).show();
            return;
        }
        if (mUseAnim) {
            // 使用动画效果刷新UI
            startAnim(mProgress, mProgress + 10);
        } else {
            // 不使用动画效果刷新UI
            mProgress += 10;
            invalidate();
        }
    }

    public void reduceTen() {
        if (-10 + mProgress < 0) {
            Toast.makeText(getContext(), "强化值不能小于0", Toast.LENGTH_LONG).show();
            return;
        }
        if (mUseAnim) {
            // 使用动画效果刷新UI
            startAnim(mProgress, mProgress - 10);
        } else {
            // 不使用动画效果刷新UI
            mProgress -= 10;
            invalidate();
        }
    }

    // 将动画开关返回
    public boolean getAnimState() {
        return mUseAnim;
    }

    public void setAnimState(boolean state) {
        mUseAnim = state;
        invalidate();
    }

    public int getPaintStyle() {
        return mStyle;
    }

    public void setPainStyle(int style) {
        mStyle = style;
        invalidate();
    }

    // 开始动画的方法
    private void startAnim(int start, final int end) {
        if (isAniming) {
            Toast.makeText(getContext(), "正在强化，请稍等...", Toast.LENGTH_LONG).show();
            return;
        }
        isAniming = true;
        final ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
        // 这里为了动画好看点就减少了一键满级动画效果所需要的时间
        if (Math.abs(start - end) > 40) animator.setDuration(mDuration * 30);
        else animator.setDuration(mDuration * Math.abs(start - end));
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mProgress = (int) valueAnimator.getAnimatedValue();

                if (end == mProgress) {
                    isAniming = false;
                }
                invalidate();
            }
        });

    }

    private void throwException() {

        if (mProgress > 100) {
            throw new RuntimeException("进度值不能设置超过100");
        }
        if (mProgress < 0) {
            throw new RuntimeException("进度值不能设置小于0");
        }
    }
}
