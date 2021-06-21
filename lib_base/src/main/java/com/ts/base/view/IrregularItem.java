package com.ts.base.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 不规则形状按钮
 */
public class IrregularItem extends AppCompatImageView {
    public interface OnSelectListener {
        void onSelect(boolean selected);
    }
    private OnSelectListener onSelectListener;
    public void setOnSelectListener(OnSelectListener l) {
        this.onSelectListener = l;
    }

    private int width = -1;
    private int height = -1;

    private Bitmap bitmap;
    private boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public IrregularItem(Context context) {
        super(context);
    }

    public IrregularItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IrregularItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (width == -1 || height == -1) {
            width = getWidth();
            height = getHeight();

            Drawable drawable = getBackground().getCurrent();
            Bitmap source = ((BitmapDrawable) drawable).getBitmap();

            bitmap = Bitmap.createScaledBitmap(source, width, height, false);
        }

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) event.getX();
                int downY = (int) event.getY();

                if (null == bitmap || downX < 0 || downY < 0 || downX >= width || downY >= height) {
                    return false;
                }

                int pixel = bitmap.getPixel(downX, downY);

                if (Color.TRANSPARENT == pixel) {
                    setSelected(false);
                } else {
                    setSelected(!selected);
                }
                return false;
//            case MotionEvent.ACTION_MOVE:
//                int moveX = (int) event.getX();
//                int moveY = (int) event.getY();
//
//                if (null == bitmap || moveX < 0 || moveY < 0 || moveX >= width || moveY >= height) {
//                    setSelected(false);
//                } else {
//                    int movePixel = bitmap.getPixel(moveX, moveY);
//
//                    if (Color.TRANSPARENT == movePixel) {
//                        setSelected(false);
//                    } else {
//                        setSelected(!selected);
//                    }
//                }
//                return super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        boolean selectedChanged = this.selected != selected;
        this.selected = selected;
        if (selectedChanged) {
            if (onSelectListener != null) {
                onSelectListener.onSelect(selected);
            }
        }
    }
}