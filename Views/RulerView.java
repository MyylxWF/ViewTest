package com.myylx.tv.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;


/**
 * myylx on 2018/7/3.
 */

public class RulerView extends View {

    public static final String TAG = "RulerView";

    private int width;
    private int height;

    private int rulerHeight;//尺子的高度

    private Paint paint;
    private Paint scalePaint;

    private int lineH = dp2px(10);//刻度线的高度
    private int scaleWidth = dp2px(2);//刻度宽度
    private int scaleSpacing = scaleWidth + dp2px(2);//刻度间距
    private int dev = 0;//偏移量

    private float oldX;//上一次的触摸点

    private GestureDetector gestureDetector;
    private Scroller scroller;
    private Context context;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context con) {
        this.context = con;
        rulerHeight = dp2px(50);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setColor(Color.BLACK);
        scalePaint.setTextSize(24);
        scalePaint.setStrokeWidth(scaleWidth);
        scroller = new Scroller(context);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "velocityX=" + velocityX);
                ViewConfiguration vcf = ViewConfiguration.get(context);
                //获取可以识别的最小fling
                int mineV = vcf.getScaledMinimumFlingVelocity();
                if (Math.abs(velocityX) > mineV) {
                    //只进行x轴的滑动
                    scroller.fling(dev, 0, (int) velocityX / 2, 0, -20000, 0, 0, 0);
                    invalidate();
                }
                return true;
            }
        });
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            //滑动过程
            dev = scroller.getCurrX();
            if (dev < 0) {
                invalidate();
            } else {
                dev = 0;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            width = right - left;
            height = bottom - top;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRulerTB(canvas);
        drawRulerScale(canvas);
    }

    private void drawRulerTB(Canvas canvas) {
        int topLine = height / 2 - rulerHeight / 2;
        int bottomLine = height / 2 + rulerHeight / 2;
        canvas.save();
        //尺子上线
        canvas.drawLine(0, topLine, width, topLine, paint);
        //尺子下线
        canvas.drawLine(0, bottomLine, width, bottomLine, paint);
        //指向中间值的线
        canvas.drawLine(width / 2, topLine - 2 * lineH, width / 2, topLine, scalePaint);
        canvas.restore();
    }

    private void drawRulerScale(Canvas canvas) {
        int topLine = height / 2 - rulerHeight / 2;
        //循环总数
        int forTotal = (width + Math.abs(dev)) / scaleSpacing;
        //开始循环数
        int forStart = Math.abs(dev) / scaleSpacing;
        //第一条线的起始x值
        int start = this.scaleWidth / 2 + dev + forStart * scaleSpacing;
        canvas.save();

        for (int i = forStart; i < forTotal; i++) {
            //刻度线
            canvas.drawLine(start, topLine, start, topLine + (i % 10 == 0 && i != 0 ? 2 * lineH : lineH), scalePaint);
            if (i % 10 == 0 && i != 0) {
                //长刻度线底下的文字
                float v2 = measureText(scalePaint, start - dev);
                canvas.drawText(String.valueOf(start - dev), start - v2 / 2, topLine + 3 * lineH, scalePaint);
            }
            //下一条刻度线的位置
            start += scaleSpacing;
        }
        //中间位置的数值
        float v1 = measureText(scalePaint, getWidth() / 2 + Math.abs(dev));
        canvas.drawText((width / 2 + Math.abs(dev) + ""), (width - v1) / 2, topLine - 2 * lineH, scalePaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //使用手势识别器判断是否需要惯性滑动
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float newX = event.getX();
                //计算偏移量
                dev += (newX - oldX);
                oldX = newX;
                if (dev <= 0) {
                    invalidate();
                } else {
                    //向左只能滑动到0位置
                    dev = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 测量字体宽度
     */
    private float measureText(Paint paint, int intt) {
        return paint.measureText(String.valueOf(intt));
    }

    private int dp2px(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * displayMetrics.density + 0.5);
    }

}
