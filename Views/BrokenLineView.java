package code.cn.ucashtwo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * myylx on 2018/7/2.
 */

public class BrokenLineView extends View {

    public static final String TAG = "ViewTestCopy";

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect fontRect = new Rect();//测量字体宽高

    private Path pathLine = new Path();
    private Context context;

    private int colorOne = 0xFF78A0DC;
    private int colorTwo = 0xFF3C9FF6;

    private String[] YPrices;
    private String[] days;
    private float[] prices;
    private float lineWidth;
    private int paddingStartBottom;
    private int paddingTopEnd;

    private float maxPrice = 10.98f;


    public BrokenLineView(Context context) {
        this(context, null);
    }

    public BrokenLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BrokenLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        this.context = context;
        YPrices = getYText(maxPrice);
        prices = getPrice(maxPrice);
        days = getDays();
        paddingStartBottom = dp2px(40);
        paddingTopEnd = dp2px(10);
        lineWidth = dp2px(2);
    }

    public void setPrice(float maxPrice) {
        this.maxPrice = maxPrice;
        YPrices = getYText(maxPrice);
        prices = getPrice(maxPrice);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawXYLine(canvas);
        drawLine(canvas);
        drawDotAndText(canvas);
    }

    private void drawDotAndText(Canvas canvas) {
        canvas.save();

        float sectionX = (getWidth() - paddingStartBottom - paddingTopEnd) / prices.length;
        float yHeight = getHeight() - paddingStartBottom - paddingTopEnd;

        //绘制原点
        for (int i = 0; i < prices.length; i++) {
            float pointY = getHeight() - paddingStartBottom - getYPricePercentage(prices[i]) * yHeight;
            paint.setStrokeWidth(dp2px(7));
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawPoint(sectionX * i + paddingStartBottom + sectionX / 2, pointY, paint);
        }

        paint.setTextSize(32);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.clearShadowLayer();

        //绘制价格
        for (int i = 0; i < prices.length; i++) {
            float pointY = getHeight() - paddingStartBottom - getYPricePercentage(prices[i]) * yHeight;
            paint.setColor(colorOne);
            paint.setStrokeWidth(lineWidth);
            String drawPrice = String.valueOf(prices[i]);
            paint.getTextBounds(drawPrice, 0, drawPrice.length(), fontRect);
            int fontW = fontRect.width();
            canvas.drawText(drawPrice, sectionX * i + paddingStartBottom - fontW / 2 + sectionX / 3, pointY - dp2px(5), paint);
        }

        canvas.restore();
    }

    private void drawLine(Canvas canvas) {
        canvas.save();
        float sectionX = (getWidth() - paddingStartBottom - paddingTopEnd) / prices.length;
        float yHeight = getHeight() - paddingStartBottom - paddingTopEnd;
        paint.setShadowLayer(3, 3, 3, colorTwo);
        paint.setColor(colorTwo);
        for (int i = 0; i < prices.length; i++) {
            float pointY = getHeight() - paddingStartBottom - getYPricePercentage(prices[i]) * yHeight;
            Log.d("Test7", String.valueOf(pointY));
            if (i == 0) {
                pathLine.moveTo(sectionX * i + paddingStartBottom + sectionX / 2, pointY);
            } else {
                pathLine.lineTo(sectionX * i + paddingStartBottom + sectionX / 2, pointY);
            }
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        canvas.drawPath(pathLine, paint);

        canvas.restore();
    }


    private void drawXYLine(Canvas canvas) {
        canvas.save();
        paint.reset();
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setTextSize(dp2px(12));
        paint.setColor(colorOne);

        //drawY
        float sectionY = (getWidth() - paddingStartBottom - paddingTopEnd) / days.length;
        for (int i = 0; i <= days.length; i++) {
            canvas.drawLine(sectionY * i + paddingStartBottom, paddingTopEnd, sectionY * i + paddingStartBottom, getHeight() - paddingStartBottom, paint);
            if (i != days.length) {
                paint.getTextBounds(days[i], 0, days[i].length(), fontRect);
                int fontW = fontRect.width();
                canvas.drawText(days[i], sectionY * i + paddingStartBottom - fontW / 2 + sectionY / 2, getHeight() - paddingStartBottom / 2, paint);
            }

        }

        //drawX
        float sectionX = (getHeight() - paddingStartBottom - paddingTopEnd) / (YPrices.length - 1);
        for (int i = YPrices.length - 1; i >= 0; i--) {
            canvas.drawLine((float) paddingStartBottom, sectionX * i + paddingTopEnd, getWidth() - paddingTopEnd, sectionX * i + paddingTopEnd, paint);
            paint.getTextBounds(YPrices[i], 0, YPrices[i].length(), fontRect);
            int fontW = fontRect.width();
            int fontH = fontRect.height();
            canvas.drawText(YPrices[i], dp2px(35) - fontW, sectionX * i + paddingTopEnd + fontH / 2, paint);
        }
        canvas.restore();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = ((size - paddingStartBottom - paddingTopEnd) / 5) * 2 + paddingStartBottom + paddingTopEnd;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public int dp2px(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private float getYPricePercentage(float price) {
        float aFloat = Float.valueOf(YPrices[YPrices.length - 1]);
        float bFloat = Float.valueOf(YPrices[0]);
        float cFloat = aFloat - bFloat;
        float dFloat = price - bFloat;
        float percentage = 1 - dFloat / cFloat;
        Log.d(TAG, "getYPricePercentage" + percentage);
        return percentage;

    }

    private String[] getYText(float maxPrice) {
        float v = maxPrice + 0.01f;
        String[] floats = new String[4];
        for (int i = 0; i < floats.length; i++) {
            BigDecimal b1 = new BigDecimal(Float.toString(v));
            BigDecimal b2 = new BigDecimal(Float.toString((0.02f * i)));
            float XSection = b1.subtract(b2).setScale(2, BigDecimal.ROUND_HALF_DOWN).floatValue();
            floats[i] = String.valueOf(XSection);
            Log.d(TAG, "" + i + "=" + XSection);
        }
        return floats;
    }

    private float[] getPrice(float maxPrice) {
        float[] prices = new float[5];
        for (int i = prices.length - 1; i >= 0; i--) {
            if (prices.length - 1 == i) {
                float maxPriceCopy = new BigDecimal(maxPrice).setScale(2, BigDecimal.ROUND_HALF_DOWN).floatValue();
                prices[prices.length - 1] = maxPriceCopy;
            } else {
                BigDecimal b1 = new BigDecimal(prices[i + 1] - 0.01f);
                prices[i] = b1.setScale(2, BigDecimal.ROUND_HALF_DOWN).floatValue();

            }
            Log.d(TAG, i + "=" + prices[i]);
        }
        return prices;
    }


    private String[] getDays() {
        Calendar instance = Calendar.getInstance(Locale.CHINA);
        String[] days = new String[5];
        for (int i = 0; i < days.length; i++) {
            SimpleDateFormat DateFormat = new SimpleDateFormat("MM.dd", Locale.CHINA);
            instance.set(Calendar.DATE, instance.get(Calendar.DATE) - (i == 0 ? 0 : 1));
            Date SevenAgoTime = instance.getTime();
            String day = DateFormat.format(SevenAgoTime);
            days[days.length - 1 - i] = day;
        }
        return days;
    }

}
