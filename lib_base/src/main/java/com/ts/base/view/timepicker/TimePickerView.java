package com.ts.base.view.timepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ts.base.R;

import java.util.Calendar;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class TimePickerView extends LinearLayout implements NumberPickerView.OnValueChangeListener {

    private static final int DEFAULT_GREGORIAN_COLOR = 0xff3388ff;
    private static final int DEFAULT_NORMAL_TEXT_COLOR = 0xFF555555;

    private static final int HOUR_START = 0;
    private static final int HOUR_STOP = 23;
    private static final int HOUR_COUNT = HOUR_STOP - HOUR_START + 1;

    private static final int MINUTE_START = 0;
    private static final int MINUTE_STOP = 59;
    private static final int MINUTE_COUNT = MINUTE_STOP - MINUTE_START + 1;

    private NumberPickerView mHourPickerView;
    private NumberPickerView mMinutePickerView;

    private int mThemeColorG = DEFAULT_GREGORIAN_COLOR;
    private int mNormalTextColor = DEFAULT_NORMAL_TEXT_COLOR;

    /**
     * display values
     */
    private String[] mDisplayHours;
    private String[] mDisplayMinutes;

    /**
     * true to use scroll anim when switch picker passively
     */
    private boolean mScrollAnim = true;

    private OnTimeChangeListener mOnTimeChangeListener;

    public TimePickerView(Context context) {
        super(context);
        initInternal(context);
    }

    public TimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initInternal(context);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(context, attrs);
        initInternal(context);
    }

    private void initInternal(Context context) {
        View contentView = inflate(context, R.layout.base_view_time_picker, this);

        mHourPickerView = (NumberPickerView) contentView.findViewById(R.id.picker_hour);
        mMinutePickerView = (NumberPickerView) contentView.findViewById(R.id.picker_minute);

        mHourPickerView.setOnValueChangedListener(this);
        mMinutePickerView.setOnValueChangedListener(this);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePickerView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.DatePickerView_scrollAnimation) {
                mScrollAnim = a.getBoolean(attr, true);
            } else if (attr == R.styleable.DatePickerView_solarThemeColor) {
                mThemeColorG = a.getColor(attr, DEFAULT_GREGORIAN_COLOR);
            }
            mThemeColorG = context.getResources().getColor(R.color.base_theme);
            if (attr == R.styleable.DatePickerView_normalTextColor) {
                mNormalTextColor = a.getColor(attr, DEFAULT_NORMAL_TEXT_COLOR);
            }
        }
        a.recycle();
    }

    public void init(Calendar cal) {
        setColor(mThemeColorG, mNormalTextColor);
        setDisplayValuesForAll(cal, false);
    }

    public void setColor(int themeColor, int normalColor) {
        setThemeColor(themeColor);
        setNormalColor(normalColor);
    }

    public void setThemeColor(int themeColor) {
        mHourPickerView.setSelectedTextColor(themeColor);
        mHourPickerView.setHintTextColor(themeColor);
        mHourPickerView.setDividerColor(themeColor);
        mMinutePickerView.setSelectedTextColor(themeColor);
        mMinutePickerView.setHintTextColor(themeColor);
        mMinutePickerView.setDividerColor(themeColor);
    }

    public void setNormalColor(int normalColor) {
        mHourPickerView.setNormalTextColor(normalColor);
        mMinutePickerView.setNormalTextColor(normalColor);
    }

    private void setDisplayValuesForAll(Calendar cal, boolean anim) {
        setDisplayData();
        initValuesForHour(cal, anim);
        initValuesForMinute(cal, anim);
    }

    private void setDisplayData() {
        if (mDisplayHours == null) {
            mDisplayHours = new String[HOUR_COUNT];
            for (int i = 0; i < HOUR_COUNT; i++) {
                mDisplayHours[i] = toStr(HOUR_START + i);
            }
        }
        if (mDisplayMinutes == null) {
            mDisplayMinutes = new String[MINUTE_COUNT];
            for (int i = 0; i < MINUTE_COUNT; i++) {
                mDisplayMinutes[i] = toStr(MINUTE_START + i);
            }
        }
    }

    //without scroll animation when init
    private void initValuesForHour(Calendar cal, boolean anim) {
        setValuesForPickerView(mHourPickerView, cal.get(Calendar.HOUR_OF_DAY), HOUR_START, HOUR_STOP, mDisplayHours, false, anim);
    }

    private void initValuesForMinute(Calendar cal, boolean anim) {
        setValuesForPickerView(mMinutePickerView, cal.get(Calendar.MINUTE), MINUTE_START, MINUTE_STOP, mDisplayMinutes, false, anim);
    }

    private void setValuesForPickerView(NumberPickerView pickerView, int newSway, int newStart, int newStop,
                                        String[] newDisplayedVales, boolean needRespond, boolean anim) {
        if (newDisplayedVales == null) {
            throw new IllegalArgumentException("newDisplayedVales should not be null.");
        } else if (newDisplayedVales.length == 0) {
            throw new IllegalArgumentException("newDisplayedVales's length should not be 0.");
        }
        int newSpan = newStop - newStart + 1;
        if (newDisplayedVales.length < newSpan) {
            throw new IllegalArgumentException("newDisplayedVales's length should not be less than newSpan.");
        }

        int oldStart = pickerView.getMinValue();
        int oldStop = pickerView.getMaxValue();
        int oldSpan = oldStop - oldStart + 1;
        int fromValue = pickerView.getValue();
        pickerView.setMinValue(newStart);
        if (newSpan > oldSpan) {
            pickerView.setDisplayedValues(newDisplayedVales);
            pickerView.setMaxValue(newStop);
        } else {
            pickerView.setMaxValue(newStop);
            pickerView.setDisplayedValues(newDisplayedVales);
        }
        if (mScrollAnim && anim) {
            int toValue = newSway;
            if (fromValue < newStart) {
                fromValue = newStart;
            }
            pickerView.smoothScrollToValue(fromValue, toValue, needRespond);
        } else {
            pickerView.setValue(newSway);
        }
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        if (mOnTimeChangeListener != null) {
            mOnTimeChangeListener.onTimeChange(mHourPickerView.getValue(), mMinutePickerView.getValue());
        }
    }

    public TimeData getTimeData() {
        return new TimeData(mHourPickerView.getValue(), mMinutePickerView.getValue());
    }

    public View getNumberPickerYear() {
        return mHourPickerView;
    }

    public View getNumberPickerMonth() {
        return mMinutePickerView;
    }

    public void setNumberPickerYearVisibility(int visibility) {
        setNumberPickerVisibility(mHourPickerView, visibility);
    }

    public void setNumberPickerMonthVisibility(int visibility) {
        setNumberPickerVisibility(mMinutePickerView, visibility);
    }

    public void setNumberPickerVisibility(NumberPickerView view, int visibility) {
        if (view.getVisibility() == visibility) {
            return;
        } else if (visibility == View.GONE || visibility == View.VISIBLE || visibility == View.INVISIBLE) {
            view.setVisibility(visibility);
        }
    }

    public interface OnTimeChangeListener {
        void onTimeChange(int hour, int minute);
    }

    public void setOnTimeSelectListener(OnTimeChangeListener l) {
        mOnTimeChangeListener = l;
    }

    private String toStr(int number) {
        return number >= 10 ? String.valueOf(number) : "0" + number;
    }

    public static class TimeData {
        private int hour;
        private int minute;

        public TimeData(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }
    }
}