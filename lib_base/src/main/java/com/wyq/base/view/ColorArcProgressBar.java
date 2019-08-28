package com.wyq.base.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.wyq.base.R;
import com.wyq.base.util.DecimalFormatUtil;
import com.wyq.base.util.MeasureUtil;

/**
 * colorful arc progress bar
 */
public class ColorArcProgressBar extends View {
    private int measureWidth;
    private int measureHeight;

    public interface OnProgressChangeListener {
        void onProgressChange(float progress, float value);
    }

    private OnProgressChangeListener onProgressChangeListener;
    public void setOnProgressChangeListener(OnProgressChangeListener l) {
        this.onProgressChangeListener = l;
    }

    public interface ContentValueFormatter {
        String getFormattedValue(float value);
    }

    private ContentValueFormatter contentValueFormatter;
    public void setContentValueFormatter(ContentValueFormatter l) {
        this.contentValueFormatter = l;
    }

    private int diameter = 500;  //直径
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标

    private Paint baseCirclePaint;
    private Paint progressPaint;
    private Paint titlePaint;
    private Paint contentPaint;
    private Paint unitPaint;

    private RectF bgRect;

    private ValueAnimator progressAnimator;
    private PaintFlagsDrawFilter mDrawFilter; // 抗锯齿
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;

    private float startAngle = 135;
    private float totalAngle = 360;
    private float currentAngle = 0;
    private float lastAngle;
    private int[] gradientColors = null;
    private float maxValue = 60;
    private float currentValue = 0;
    private float baseCircleWidth; // 基础环宽度
    private int baseCircleColor;
    private float circleWidth;
    private int animationDuration = 300;

    private String titleString;
    private float titleSize;
    private int titleColor;

    private String contentString;
    private float contentSize;
    private int contentColor;

    private String unitString;
    private float unitSize;
    private int unitColor;

    private boolean showContent;

    private float k; // totalAngle / maxValue 的值

    public ColorArcProgressBar(Context context) {
        super(context, null);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initConfig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     *
     * @param context
     * @param attrs
     */
    private void initConfig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);
        int startColor = a.getColor(R.styleable.ColorArcProgressBar_pb_startColor, Color.GREEN);
        int middleColor = a.getColor(R.styleable.ColorArcProgressBar_pb_middleColor, startColor);
        int endColor = a.getColor(R.styleable.ColorArcProgressBar_pb_endColor, startColor);
        gradientColors = new int[]{startColor, middleColor, endColor, middleColor, startColor};

        startAngle = a.getInteger(R.styleable.ColorArcProgressBar_pb_startAngle, 135);
        totalAngle = a.getInteger(R.styleable.ColorArcProgressBar_pb_totalAngle, 360);
        baseCircleWidth = a.getDimension(R.styleable.ColorArcProgressBar_pb_baseCircleWidth, 10);
        baseCircleColor = a.getColor(R.styleable.ColorArcProgressBar_pb_baseCircleColor, Color.BLACK);
        circleWidth = a.getDimension(R.styleable.ColorArcProgressBar_pb_circleWidth, 10);
        showContent = a.getBoolean(R.styleable.ColorArcProgressBar_pb_showContent, false);
        titleString = a.getString(R.styleable.ColorArcProgressBar_pb_title);
        titleSize = a.getDimension(R.styleable.ColorArcProgressBar_pb_titleSize, 10);
        titleColor = a.getColor(R.styleable.ColorArcProgressBar_pb_titleColor, Color.BLACK);
        contentString = a.getString(R.styleable.ColorArcProgressBar_pb_content);
        contentSize = a.getDimension(R.styleable.ColorArcProgressBar_pb_contentSize, 20);
        contentColor = a.getColor(R.styleable.ColorArcProgressBar_pb_contentColor, Color.BLACK);
        unitString = a.getString(R.styleable.ColorArcProgressBar_pb_unit);
        unitSize = a.getDimension(R.styleable.ColorArcProgressBar_pb_unitSize, 10);
        unitColor = a.getColor(R.styleable.ColorArcProgressBar_pb_unitColor, Color.BLACK);
        currentValue = a.getFloat(R.styleable.ColorArcProgressBar_pb_currentValue, 0);
        maxValue = a.getFloat(R.styleable.ColorArcProgressBar_pb_maxValue, 100);
        setCurrentValues(currentValue);
        setMaxValue(maxValue);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureWidth = MeasureUtil.measureWidthOrHeight(widthMeasureSpec);
        measureHeight = MeasureUtil.measureWidthOrHeight(heightMeasureSpec);

        float offset = baseCircleWidth > circleWidth ? baseCircleWidth / 2 : circleWidth / 2;

        // 弧形的矩阵区域
        bgRect = new RectF();
        if (measureWidth > measureHeight) {
            bgRect.left = (measureWidth - measureHeight) / 2f + offset;
            bgRect.top = 0 + offset;
            bgRect.right = measureWidth - (measureWidth - measureHeight) / 2f - offset;
            bgRect.bottom = measureHeight - offset;
        } else {
            bgRect.left = 0 + offset;
            bgRect.top = (measureHeight - measureWidth) / 2f + offset;
            bgRect.right = measureHeight - offset;
            bgRect.bottom = measureHeight - (measureHeight - measureWidth) / 2f - offset;
        }
        centerX = (bgRect.right - bgRect.left) / 2;
        centerY = (bgRect.bottom - bgRect.top) / 2;

        setMeasuredDimension(measureWidth, measureHeight);
    }

    private void initView() {
        // 整个弧形
        baseCirclePaint = new Paint();
        baseCirclePaint.setColor(baseCircleColor);
        baseCirclePaint.setStrokeWidth(baseCircleWidth);
        baseCirclePaint.setAntiAlias(true);
        baseCirclePaint.setStyle(Paint.Style.STROKE);
        baseCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        // 当前进度的弧形
        progressPaint = new Paint();
        progressPaint.setStrokeWidth(circleWidth);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // 显示标题文字
        titlePaint = new Paint();
        titlePaint.setColor(titleColor);
        titlePaint.setTextSize(titleSize);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        // 内容显示文字
        contentPaint = new Paint();
        contentPaint.setColor(contentColor);
        contentPaint.setTextSize(contentSize);
        contentPaint.setTextAlign(Paint.Align.CENTER);

        // 显示单位文字
        unitPaint = new Paint();
        unitPaint.setColor(unitColor);
        unitPaint.setTextSize(unitSize);
        unitPaint.setTextAlign(Paint.Align.CENTER);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        sweepGradient = new SweepGradient(centerX, centerY, gradientColors, null);
        rotateMatrix = new Matrix();

        LogUtils.d("centerX: " + centerX + ",centerY: " + centerY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        // 整个弧
        canvas.drawArc(bgRect, startAngle, totalAngle, false, baseCirclePaint);

        // 设置渐变色
        rotateMatrix.setRotate(startAngle, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(sweepGradient);

        // 当前进度
        canvas.drawArc(bgRect, startAngle, currentAngle, false, progressPaint);

        if (!TextUtils.isEmpty(titleString)) {
            canvas.drawText(titleString, centerX, centerY - contentSize, titlePaint);
        }

        if (showContent) {
            if (!TextUtils.isEmpty(contentString)) {
                canvas.drawText(contentString, centerX, centerY + contentSize / 2, contentPaint);
            } else if (contentValueFormatter != null) {
                canvas.drawText(contentValueFormatter.getFormattedValue(currentValue), centerX, centerY + contentSize / 2, contentPaint);
            } else {
                canvas.drawText(DecimalFormatUtil.format(currentValue, DecimalFormatUtil.number), centerX, centerY + contentSize / 2, contentPaint);
            }
        }
        if (!TextUtils.isEmpty(unitString)) {
            canvas.drawText(unitString, centerX, centerY + contentSize + unitSize / 2, unitPaint);
        }
//        invalidate();
    }

    /**
     * 设置最大值
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        k = totalAngle / maxValue;
    }

    public void setProgress(float progress) {
        LogUtils.d("progress:" + progress);
        setCurrentValues(progress * maxValue);
    }

    /**
     * 设置当前值
     *
     * @param currentValues
     */
    public void setCurrentValues(float currentValues) {
        if (currentValues > maxValue) {
            currentValues = maxValue;
        }
        if (currentValues < 0) {
            currentValues = 0;
        }
        this.currentValue = currentValues;
        lastAngle = currentAngle;
        setAnimation(lastAngle, currentValues * k, animationDuration);

        if (onProgressChangeListener != null) {

        }
    }

    /**
     * 设置整个圆弧宽度
     *
     * @param baseCircleWidth
     */
    public void setBaseCircleWidth(int baseCircleWidth) {
        this.baseCircleWidth = baseCircleWidth;
    }

    /**
     * 设置进度宽度
     *
     * @param circleWidth
     */
    public void setCircleWidth(int circleWidth) {
        this.circleWidth = circleWidth;
    }

    /**
     * 设置单位文字
     *
     * @param hintString
     */
    public void setUnit(String hintString) {
        this.unitString = hintString;
        invalidate();
    }

    public void setContent(String contentString) {
        this.contentString = contentString;
        invalidate();
    }

    public float getMaxValue() {
        return maxValue;
    }

    /**
     * 设置直径大小
     *
     * @param diameter
     */
    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    /**
     * 为进度设置动画
     *
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length) {
        progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(currentAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) animation.getAnimatedValue();
//            currentValue = currentAngle / k;
                invalidate();
            }
        });
        progressAnimator.start();
    }

}
