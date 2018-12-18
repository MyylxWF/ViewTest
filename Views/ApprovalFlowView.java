package com.myylx.tv.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.myylx.tv.util.Utils;

import java.util.ArrayList;

/**
 * Android on 2018/12/14.
 */

public class ApprovalFlowView extends View {

    public static final String TAG = "ApprovalFlowView";

    private int HORIZONTAL = -1;
    private int VERTICAL = -2;
    private int currentOrientation = HORIZONTAL;
    private int C_7EB2FC = 0xff7EB2FC;//青色
    private int C_5559AF = 0xff5559AF;//紫色
    private int C_FF8C22 = 0xffFF8C22;//橙色

    private int paddingTB = Utils.dp2px(10);//padding TopBottom
    private int paddingSE = Utils.dp2px(40);//padding StartEnd
    private int diameter = Utils.dp2px(46);//圆直径
    private int radius = diameter / 2;//圆半径
    private int dateHeight = Utils.dp2px(20);//横向模式下时间区域的高度


    private Paint paintOne = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintTwo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path pathLine = new Path();
    private PathDashPathEffect pdpEffect;

    private Rect fontRect = new Rect();//测量字体宽高
    private RectF tagRectF = new RectF();//tag 背景

    private ArrayList<Text> data;

    public ApprovalFlowView(Context context) {
        this(context, null);
    }

    public ApprovalFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ApprovalFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        //虚线
        Path effectPath = new Path();
        effectPath.addCircle(0, 0, 3, Path.Direction.CW);
        pdpEffect = new PathDashPathEffect(effectPath, 15, 0, PathDashPathEffect.Style.ROTATE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (currentOrientation == HORIZONTAL) {
            //横向
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(paddingTB * 2 + diameter + dateHeight, MeasureSpec.EXACTLY);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else {
            //竖向
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(paddingTB * 2 + (int) (diameter * 2.6 + 0.5), MeasureSpec.EXACTLY);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(diameter + paddingSE * 2, MeasureSpec.EXACTLY);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null) return;
        drawDashLine(canvas);
        drawTag(canvas);
        drawCircle(canvas);
        drawTime(canvas);
    }

    private void drawTime(Canvas canvas) {
        if (currentOrientation == HORIZONTAL) {
            paintTwo.setColor(Color.BLACK);
            paintTwo.setTextSize(dp2px(14));
            paintTwo.setStrokeWidth(1);
            int circleSpacing = (getWidth() - diameter - paddingSE * 2) / (data.size() - 1);
            for (int i = 0; i < data.size(); i++) {
                String time = data.get(i).getTime();
                if (TextUtils.isEmpty(time)) continue;
                float x = paddingSE + i * circleSpacing;
                float y = getHeight() - dp2px(5);
                canvas.drawText(time, x, y, paintTwo);
            }
        }

    }

    private void drawCircle(Canvas canvas) {

        int circleSpacing = 0;
        if (currentOrientation == HORIZONTAL) {
            circleSpacing = (getWidth() - diameter - paddingSE * 2) / (data.size() - 1);
        } else {
            if (data.size() > 1) {
                circleSpacing = (getHeight() - diameter - paddingTB * 2) / (data.size() - 1);
            }
        }
        for (int i = 0; i < data.size(); i++) {
            //draw circle background
            paintTwo.setColor(Color.WHITE);
            paintTwo.setStyle(Paint.Style.FILL);
            if (currentOrientation == HORIZONTAL) {
                canvas.drawCircle(paddingSE + radius + i * circleSpacing, paddingTB + radius, radius, paintTwo);
            } else {
                if (data.size() == 1) {
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paintTwo);
                } else {
                    canvas.drawCircle(paddingSE + radius, paddingTB + radius + i * circleSpacing, radius, paintTwo);
                }
            }
            //draw circle line
            paintTwo.setColor(C_7EB2FC);
            paintTwo.setStyle(Paint.Style.STROKE);
            paintTwo.setStrokeWidth(Utils.dp2px(1));
            if (currentOrientation == HORIZONTAL) {
                canvas.drawCircle(paddingSE + radius + i * circleSpacing, paddingTB + radius, radius, paintTwo);
            } else {
                if (data.size() == 1) {
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paintTwo);
                } else {
                    canvas.drawCircle(paddingSE + radius, paddingTB + radius + i * circleSpacing, radius, paintTwo);
                }
            }
            //draw circle text
            paintTwo.setColor(Color.BLACK);
            paintTwo.setTextSize(dp2px(14));
            paintTwo.setStrokeWidth(1);
            paintTwo.setStyle(Paint.Style.FILL);
            String name = data.get(i).getName();
            if (TextUtils.isEmpty(name)) continue;
            paintTwo.getTextBounds(name, 0, name.length(), fontRect);
            int fontW = fontRect.width();
            int fontH = fontRect.height();
            if (currentOrientation == HORIZONTAL) {
                canvas.drawText(name, paddingSE + radius + i * circleSpacing - fontW / 2, paddingTB + radius + fontH / 2, paintTwo);
            } else {
                if (data.size() == 1) {
                    if (name.length() > 3) {
                        String nameOne = name.substring(0, 2);
                        String nameTwo = name.substring(2, 4);
                        canvas.drawText(nameOne, getWidth() / 2 - fontW / 4, getHeight() / 2 - dp2px(1), paintTwo);
                        canvas.drawText(nameTwo, getWidth() / 2 - fontW / 4, getHeight() / 2 + dp2px(1) + fontH, paintTwo);
                    } else {
                        canvas.drawText(name, getWidth() / 2 - fontW / 2, getHeight() / 2 + fontH / 2, paintTwo);
                    }
                } else {
                    if (name.length() > 3) {
                        String nameOne = name.substring(0, 2);
                        String nameTwo = name.substring(2, 4);
                        canvas.drawText(nameOne, paddingSE + radius - fontW / 4, paddingTB + radius + i * circleSpacing - dp2px(1), paintTwo);
                        canvas.drawText(nameTwo, paddingSE + radius - fontW / 4, paddingTB + radius + i * circleSpacing + dp2px(1) + fontH, paintTwo);
                    } else {
                        canvas.drawText(name, paddingSE + radius - fontW / 2, paddingTB + radius + i * circleSpacing + fontH / 2, paintTwo);
                    }
                }
            }
        }

    }

    private void drawTag(Canvas canvas) {

        if (currentOrientation == VERTICAL && data.size() == 1) {
            //vertical && size==1   暂时不进行tag设置,后续功能再说
            return;
        }

        int circleSpacing;
        if (currentOrientation == HORIZONTAL) {
            circleSpacing = (getWidth() - diameter - paddingSE * 2) / (data.size() - 1);
        } else {
            circleSpacing = (getHeight() - diameter - paddingTB * 2) / (data.size() - 1);
        }
        paintTwo.setStyle(Paint.Style.FILL);
        paintTwo.setTextSize(dp2px(10));
        for (int i = 0; i < data.size(); i++) {
            String tag = data.get(i).getTag();
            if (TextUtils.isEmpty(tag)) continue;
            paintTwo.getTextBounds(tag, 0, tag.length(), fontRect);
            int fontW = fontRect.width();
            int fontH = fontRect.height();
            paintTwo.setColor(C_7EB2FC);
            if (currentOrientation == HORIZONTAL) {
                float left = paddingSE + radius + i * circleSpacing;
                float top = diameter + paddingTB - (fontH + dp2px(4));
                float right = paddingSE + radius + i * circleSpacing + radius + fontW + dp2px(2);
                float bottom = paddingTB + diameter;
                tagRectF.set(left, top, right, bottom);
            } else {
                float left = paddingSE + radius;
                float top = diameter + paddingTB + i * circleSpacing - (fontH + dp2px(4));
                float right = paddingSE + radius + radius + fontW + dp2px(2);
                float bottom = paddingTB + diameter + i * circleSpacing;
                tagRectF.set(left, top, right, bottom);
            }

            Log.d(TAG, tagRectF.toString());
            canvas.drawRoundRect(tagRectF, dp2px(10), dp2px(10), paintTwo);

            paintTwo.setColor(Color.WHITE);
            paintTwo.setStrokeWidth(1);
            if (currentOrientation == HORIZONTAL) {
                canvas.drawText(tag, paddingSE + radius + i * circleSpacing + radius - radius / 6, paddingTB + diameter - dp2px(3), paintTwo);
            } else {
                canvas.drawText(tag, paddingSE + radius + radius - radius / 6, paddingTB + diameter + i * circleSpacing + -dp2px(3), paintTwo);
            }
        }

    }


    /*绘制虚线*/
    private void drawDashLine(Canvas canvas) {
        paintOne.setColor(C_5559AF);
        paintOne.setStyle(Paint.Style.STROKE);
        paintOne.setStrokeWidth(3);
        paintOne.setPathEffect(pdpEffect);

        if (currentOrientation == HORIZONTAL) {
            //横向绘制虚线,端点分别是:开始结束圆的圆心
            int lineY = paddingTB + radius;
            pathLine.moveTo(paddingSE + radius, lineY);
            pathLine.lineTo(getWidth() - paddingSE - radius, lineY);
            canvas.drawPath(pathLine, paintOne);
        } else {
            //竖向只有数据大于1时才会绘制虚线
            if (data.size() > 1) {
                int lineX = paddingSE + radius;
                pathLine.moveTo(lineX, paddingTB + radius);
                pathLine.lineTo(lineX, getHeight() - paddingTB - radius);
                canvas.drawPath(pathLine, paintOne);
            }
        }


    }

    /*list:圆需要填充的数据  isVertical:是否是竖向*/
    public void setData(ArrayList<Text> list, boolean isVertical) {
        if (list == null) {
            throw new RuntimeException("list not null");
        }
        int listSize = list.size();
        if (isVertical) {
            //竖向数据不能小于0或大于2
            if (listSize < 0 || listSize > 2) {
                throw new RuntimeException("vertical type list.size no <0 or >2");
            }
        } else {
            //横向数据不能小于2或大于4
            if (listSize < 2 || listSize > 4) {
                throw new RuntimeException("horizontal type list.size no <2 or >4");
            }
        }
        data = list;
        currentOrientation = isVertical ? VERTICAL : HORIZONTAL;
        requestLayout();
        invalidate();
    }

    public static int dp2px(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * displayMetrics.density + 0.5);
    }

    public static class Text {
        private String name;
        private String tag;
        private String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}
