package com.ts.base.view.timepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Calendar;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import com.ts.base.R;
import com.ts.base.util.ChineseCalendar;
import com.ts.base.util.ChineseCalendarUtil;

@SuppressWarnings("WrongConstant")
public class DatePickerView extends LinearLayout implements NumberPickerView.OnValueChangeListener {

    private static final int DEFAULT_GREGORIAN_COLOR = 0xff3388ff;
    private static final int DEFAULT_LUNAR_COLOR = 0xffee5544;
    private static final int DEFAULT_NORMAL_TEXT_COLOR = 0xFF555555;

    private static final int YEAR_START = 1901;
    private static final int YEAR_STOP = 2100;
    private static final int YEAR_SPAN = YEAR_STOP - YEAR_START + 1;

    private static final int MONTH_START = 1;
    private static final int MONTH_START_GREGORIAN = 1;
    private static final int MONTH_STOP_GREGORIAN = 12;
    private static final int MONTH_SPAN_GREGORIAN = MONTH_STOP_GREGORIAN - MONTH_START_GREGORIAN + 1;

    private static final int MONTH_START_LUNAR = 1;

    private static final int MONTH_START_LUNAR_NORMAL = 1;
    private static final int MONTH_STOP_LUNAR_NORMAL = 12;
    private static final int MONTH_SPAN_LUNAR_NORMAL = MONTH_STOP_LUNAR_NORMAL - MONTH_START_LUNAR_NORMAL + 1;

    private static final int MONTH_START_LUNAR_LEAP = 1;
    private static final int MONTH_STOP_LUNAR_LEAP = 13;
    private static final int MONTH_SPAN_LUNAR_LEAP = MONTH_STOP_LUNAR_LEAP - MONTH_START_LUNAR_LEAP + 1;

    private static final int DAY_START = 1;
    private static final int DAY_STOP = 30;

    private static final int DAY_START_GREGORIAN = 1;
    private static final int DAY_STOP_GREGORIAN = 31;
    private static final int DAY_SPAN_GREGORIAN = DAY_STOP_GREGORIAN - DAY_START_GREGORIAN + 1;

    private static final int DAY_START_LUNAR = 1;
    private static final int DAY_STOP_LUNAR = 30;
    private static final int DAY_SPAN_LUNAR = DAY_STOP_LUNAR - DAY_START_LUNAR + 1;

    private NumberPickerView mYearPickerView;
    private NumberPickerView mMonthPickerView;
    private NumberPickerView mDayPickerView;

    private int mThemeColorG = DEFAULT_GREGORIAN_COLOR;
    private int mThemeColorL = DEFAULT_LUNAR_COLOR;
    private int mNormalTextColor = DEFAULT_NORMAL_TEXT_COLOR;

    /**
     * display values
     */
    private String[] mDisplayYearsGregorian;
    private String[] mDisplayMonthsGregorian;
    private String[] mDisplayDaysGregorian;
    private String[] mDisplayYearsLunar;
    private String[] mDisplayMonthsLunar;
    private String[] mDisplayDaysLunar;

    /**
     * display values for current displayed months in lunar mode
     */
    private String[] mCurrDisplayMonthsLunar;

    private boolean mIsGregorian = true;//true is gregorian mode

    /**
     * true to use scroll anim when switch picker passively
     */
    private boolean mScrollAnim = true;

    private OnDateChangedListener mOnDateChangedListener;

    public DatePickerView(Context context) {
        super(context);
        initInternal(context);
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initInternal(context);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(context, attrs);
        initInternal(context);
    }

    private void initInternal(Context context) {
        View contentView = inflate(context, R.layout.base_view_date_picker, this);

        mYearPickerView = (NumberPickerView) contentView.findViewById(R.id.picker_year);
        mMonthPickerView = (NumberPickerView) contentView.findViewById(R.id.picker_month);
        mDayPickerView = (NumberPickerView) contentView.findViewById(R.id.picker_day);

        mYearPickerView.setOnValueChangedListener(this);
        mMonthPickerView.setOnValueChangedListener(this);
        mDayPickerView.setOnValueChangedListener(this);
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
            if (attr == R.styleable.DatePickerView_lunarThemeColor) {
                mThemeColorL = a.getColor(attr, DEFAULT_LUNAR_COLOR);
            }
            mThemeColorL = context.getResources().getColor(R.color.base_theme);
            if (attr == R.styleable.DatePickerView_normalTextColor) {
                mNormalTextColor = a.getColor(attr, DEFAULT_NORMAL_TEXT_COLOR);
            }
        }
        a.recycle();
    }

    public void init() {
        setColor(mThemeColorG, mNormalTextColor);
        setConfigs(Calendar.getInstance(), true, false);
    }

    public void init(Calendar calendar) {
        setColor(mThemeColorG, mNormalTextColor);
        setConfigs(calendar, true, false);
    }

    public void init(Calendar calendar, boolean isGregorian) {
        setColor(isGregorian ? mThemeColorG : mThemeColorL, mNormalTextColor);
        setConfigs(calendar, isGregorian, false);
    }

    private void setConfigs(Calendar c, boolean isGregorian, boolean anim) {
        if (c == null) {
            c = Calendar.getInstance();
        }
        if (!checkCalendarAvailable(c, YEAR_START, YEAR_STOP, isGregorian)) {
            c = adjustCalendarByLimit(c, YEAR_START, YEAR_STOP, isGregorian);
        }
        mIsGregorian = isGregorian;
        ChineseCalendar cc;
        if (c instanceof ChineseCalendar) {
            cc = (ChineseCalendar) c;
        } else {
            cc = new ChineseCalendar(c);
        }
        setDisplayValuesForAll(cc, mIsGregorian, anim);
    }

    private Calendar adjustCalendarByLimit(Calendar c, int yearStart, int yearStop, boolean isGregorian) {
        int yearGrego = c.get(Calendar.YEAR);
        if (isGregorian) {
            if (yearGrego < yearStart) {
                c.set(Calendar.YEAR, yearStart);
                c.set(Calendar.MONTH, MONTH_START_GREGORIAN);
                c.set(Calendar.DAY_OF_MONTH, DAY_START_GREGORIAN);
            }
            if (yearGrego > yearStop) {
                c.set(Calendar.YEAR, yearStop);
                c.set(Calendar.MONTH, MONTH_STOP_GREGORIAN - 1);
                int daySway = ChineseCalendarUtil.getSumOfDayInMonthForGregorianByMonth(yearStop, MONTH_STOP_GREGORIAN);
                c.set(Calendar.DAY_OF_MONTH, daySway);
            }
        } else {
            if (Math.abs(yearGrego - yearStart) < Math.abs(yearGrego - yearStop)) {
                c = new ChineseCalendar(true, yearStart, MONTH_START_LUNAR, DAY_START_LUNAR);
            } else {
                int daySway = ChineseCalendarUtil.getSumOfDayInMonthForLunarByMonthLunar(yearStop, MONTH_STOP_LUNAR_NORMAL);
                c = new ChineseCalendar(true, yearStop, MONTH_STOP_LUNAR_NORMAL, daySway);
            }
        }
        return c;
    }

    public void toGregorianMode() {
        toGregorianMode(true);
    }

    public void toGregorianMode(boolean anim) {
        setThemeColor(mThemeColorG);
        setGregorian(true, anim);
    }

    public void toLunarMode() {
        toLunarMode(true);
    }

    public void toLunarMode(boolean anim) {
        setThemeColor(mThemeColorL);
        setGregorian(false, anim);
    }

    public void setColor(int themeColor, int normalColor) {
        setThemeColor(themeColor);
        setNormalColor(normalColor);
    }

    public void setThemeColor(int themeColor) {
        mYearPickerView.setSelectedTextColor(themeColor);
        mYearPickerView.setHintTextColor(themeColor);
        mYearPickerView.setDividerColor(themeColor);
        mMonthPickerView.setSelectedTextColor(themeColor);
        mMonthPickerView.setHintTextColor(themeColor);
        mMonthPickerView.setDividerColor(themeColor);
        mDayPickerView.setSelectedTextColor(themeColor);
        mDayPickerView.setHintTextColor(themeColor);
        mDayPickerView.setDividerColor(themeColor);
    }

    public void setNormalColor(int normalColor) {
        mYearPickerView.setNormalTextColor(normalColor);
        mMonthPickerView.setNormalTextColor(normalColor);
        mDayPickerView.setNormalTextColor(normalColor);
    }

    private void setDisplayValuesForAll(ChineseCalendar cc, boolean isGregorian, boolean anim) {
        setDisplayData(isGregorian);
        initValuesForY(cc, isGregorian, anim);
        initValuesForM(cc, isGregorian, anim);
        initValuesForD(cc, isGregorian, anim);
    }

    /**
     * @param isGregorian true is gregorian mode
     */
    private void setDisplayData(boolean isGregorian) {

        if (isGregorian) {
            if (mDisplayYearsGregorian == null) {
                mDisplayYearsGregorian = new String[YEAR_SPAN];
                for (int i = 0; i < YEAR_SPAN; i++) {
                    mDisplayYearsGregorian[i] = String.valueOf(YEAR_START + i);
                }
            }
            if (mDisplayMonthsGregorian == null) {
                mDisplayMonthsGregorian = new String[MONTH_SPAN_GREGORIAN];
                for (int i = 0; i < MONTH_SPAN_GREGORIAN; i++) {
                    mDisplayMonthsGregorian[i] = String.valueOf(MONTH_START_GREGORIAN + i);
                }
            }
            if (mDisplayDaysGregorian == null) {
                mDisplayDaysGregorian = new String[DAY_SPAN_GREGORIAN];
                for (int i = 0; i < DAY_SPAN_GREGORIAN; i++) {
                    mDisplayDaysGregorian[i] = String.valueOf(DAY_START_GREGORIAN + i);
                }
            }
        } else {
            if (mDisplayYearsLunar == null) {
                mDisplayYearsLunar = new String[YEAR_SPAN];
                for (int i = 0; i < YEAR_SPAN; i++) {
                    mDisplayYearsLunar[i] = ChineseCalendarUtil.getLunarNameOfYear(i + YEAR_START);
                }
            }
            if (mDisplayMonthsLunar == null) {
                mDisplayMonthsLunar = new String[MONTH_SPAN_GREGORIAN];
                for (int i = 0; i < MONTH_SPAN_GREGORIAN; i++) {
                    mDisplayMonthsLunar[i] = ChineseCalendarUtil.getLunarNameOfMonth(i + 1);
                }
            }
            if (mDisplayDaysLunar == null) {
                mDisplayDaysLunar = new String[DAY_SPAN_LUNAR];
                for (int i = 0; i < DAY_SPAN_LUNAR; i++) {
                    mDisplayDaysLunar[i] = ChineseCalendarUtil.getLunarNameOfDay(i + 1);
                }
            }
        }
    }

    //without scroll animation when init
    private void initValuesForY(ChineseCalendar cc, boolean isGregorian, boolean anim) {
        if (isGregorian) {
            int yearSway = cc.get(Calendar.YEAR);
            setValuesForPickerView(mYearPickerView, yearSway, YEAR_START, YEAR_STOP, mDisplayYearsGregorian, false, anim);
        } else {
            int yearSway = cc.get(ChineseCalendar.CHINESE_YEAR);
            setValuesForPickerView(mYearPickerView, yearSway, YEAR_START, YEAR_STOP, mDisplayYearsLunar, false, anim);
        }
    }

    private void initValuesForM(ChineseCalendar cc, boolean isGregorian, boolean anim) {
        int monthStart;
        int monthStop;
        int monthSway;
        String[] newDisplayedVales = null;
        if (isGregorian) {
            monthStart = MONTH_START_GREGORIAN;
            monthStop = MONTH_STOP_GREGORIAN;
            monthSway = cc.get(Calendar.MONTH) + 1;
            newDisplayedVales = mDisplayMonthsGregorian;
        } else {
            int monthLeap = ChineseCalendarUtil.getMonthLeapByYear(cc.get(ChineseCalendar.CHINESE_YEAR));
            if (monthLeap == 0) {
                monthStart = MONTH_START_LUNAR_NORMAL;
                monthStop = MONTH_STOP_LUNAR_NORMAL;
                monthSway = cc.get(ChineseCalendar.CHINESE_MONTH);
                newDisplayedVales = mDisplayMonthsLunar;
            } else {
                monthStart = MONTH_START_LUNAR_LEAP;
                monthStop = MONTH_STOP_LUNAR_LEAP;
                monthSway = ChineseCalendarUtil.convertMonthLunarToMonthSway(cc.get(ChineseCalendar.CHINESE_MONTH), monthLeap);
                newDisplayedVales = ChineseCalendarUtil.getLunarMonthsNamesWithLeap(monthLeap);
            }
        }
        setValuesForPickerView(mMonthPickerView, monthSway, monthStart, monthStop, newDisplayedVales, false, anim);
    }

    private void initValuesForD(ChineseCalendar cc, boolean isGregorian, boolean anim) {
        if (isGregorian) {
            int dayStart = DAY_START_GREGORIAN;
            int dayStop = ChineseCalendarUtil.getSumOfDayInMonthForGregorianByMonth(cc.get(Calendar.YEAR), cc.get(Calendar.MONTH) + 1);
            int daySway = cc.get(Calendar.DAY_OF_MONTH);
            mDayPickerView.setHintText(getContext().getResources().getString(R.string.day));
            setValuesForPickerView(mDayPickerView, daySway, dayStart, dayStop, mDisplayDaysGregorian, false, anim);
        } else {
            int dayStart = DAY_START_LUNAR;
            int dayStop = ChineseCalendarUtil.getSumOfDayInMonthForLunarByMonthLunar(cc.get(ChineseCalendar.CHINESE_YEAR), cc.get(ChineseCalendar.CHINESE_MONTH));
            int daySway = cc.get(ChineseCalendar.CHINESE_DATE);
            mDayPickerView.setHintText("");
            setValuesForPickerView(mDayPickerView, daySway, dayStart, dayStop, mDisplayDaysLunar, false, anim);
        }
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
        if (picker == null) return;
        if (picker == mYearPickerView) {
            passiveUpdateMonthAndDay(oldVal, newVal, mIsGregorian);
        } else if (picker == mMonthPickerView) {
            int fixYear = mYearPickerView.getValue();
            passiveUpdateDay(fixYear, fixYear, oldVal, newVal, mIsGregorian);
        } else if (picker == mDayPickerView) {
            if (mOnDateChangedListener != null) {
                DateData dateData = getCalendarData();
                mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
            }
        }
    }

    private void passiveUpdateMonthAndDay(int oldYearFix, int newYearFix, boolean isGregorian) {
        int oldMonthSway = mMonthPickerView.getValue();
        int oldDaySway = mDayPickerView.getValue();

        if (isGregorian) {
            int newMonthSway = oldMonthSway;
            int oldDayStop = ChineseCalendarUtil.getSumOfDayInMonth(oldYearFix, oldMonthSway, true);
            int newDayStop = ChineseCalendarUtil.getSumOfDayInMonth(newYearFix, newMonthSway, true);

            if (oldDayStop == newDayStop) {
                if (mOnDateChangedListener != null) {
                    DateData dateData = getCalendarData(newYearFix, newMonthSway, oldDaySway, isGregorian);
                    mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
                }
                return;
            }
            int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
            setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDaysGregorian, true, true);
            if (mOnDateChangedListener != null) {
                DateData dateData = getCalendarData(newYearFix, newMonthSway, newDaySway, isGregorian);
                mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
            }
            return;
        } else {
            int newMonthSway = 0;

            int newYearFixMonthLeap = ChineseCalendarUtil.getMonthLeapByYear(newYearFix);//1.计算当前year是否有闰月
            int oldYearFixMonthLeap = ChineseCalendarUtil.getMonthLeapByYear(oldYearFix);//2.计算之前year是否有闰月

            if (newYearFixMonthLeap == oldYearFixMonthLeap) {
                //only update day picker
                newMonthSway = oldMonthSway;

                int oldMonthLunar = ChineseCalendarUtil.convertMonthSwayToMonthLunar(oldMonthSway, oldYearFixMonthLeap);
                int newMonthLunar = ChineseCalendarUtil.convertMonthSwayToMonthLunar(newMonthSway, newYearFixMonthLeap);
                int oldDayStop = ChineseCalendarUtil.getSumOfDayInMonthForLunarByMonthLunar(oldYearFix, oldMonthLunar);
                int newDayStop = ChineseCalendarUtil.getSumOfDayInMonthForLunarByMonthLunar(newYearFix, newMonthLunar);

                if (oldDayStop == newDayStop) {
                    if (mOnDateChangedListener != null) {
                        DateData dateData = getCalendarData(newYearFix, newMonthSway, oldDaySway, isGregorian);
                        mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
                    }
                    return;
                } else {
                    int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
                    setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDaysLunar, true, true);
                    if (mOnDateChangedListener != null) {
                        DateData dateData = getCalendarData(newYearFix, newMonthSway, newDaySway, isGregorian);
                        mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
                    }
                    return;
                }
            } else {
                //由于月视图需要更新，也就是产生或者消失了闰月，更新的是newMonthSway，如果要保证不产生视觉上的变化，那么这里的newMonthSway就需要稍作改动
                //月视图需要更新
                mCurrDisplayMonthsLunar = ChineseCalendarUtil.getLunarMonthsNamesWithLeap(newYearFixMonthLeap);

                //优化方案
                int oldMonthLunar = ChineseCalendarUtil.convertMonthSwayToMonthLunar(oldMonthSway, oldYearFixMonthLeap);
                int oldMonthLunarAbs = Math.abs(oldMonthLunar);
                newMonthSway = ChineseCalendarUtil.convertMonthLunarToMonthSway(oldMonthLunarAbs, newYearFixMonthLeap);
                setValuesForPickerView(mMonthPickerView, newMonthSway, MONTH_START_LUNAR,
                        newYearFixMonthLeap == 0 ? MONTH_STOP_LUNAR_NORMAL : MONTH_STOP_LUNAR_LEAP, mCurrDisplayMonthsLunar, false, true);

                //日视图需要更新
                int oldDayStop = ChineseCalendarUtil.getSumOfDayInMonth(oldYearFix, oldMonthSway, false);
                int newDayStop = ChineseCalendarUtil.getSumOfDayInMonth(newYearFix, newMonthSway, false);
                if (oldDayStop == newDayStop) {
                    if (mOnDateChangedListener != null) {
                        DateData dateData = getCalendarData(newYearFix, newMonthSway, oldDaySway, isGregorian);
                        mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
                    }
                    return;//不需要更新
                } else {
                    int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
                    setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDaysLunar, true, true);
                    if (mOnDateChangedListener != null) {
                        DateData dateData = getCalendarData(newYearFix, newMonthSway, newDaySway, isGregorian);
                        mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
                    }
                    return;
                }
            }
        }
    }

    private void passiveUpdateDay(int oldYear, int newYear, int oldMonth, int newMonth, boolean isGregorian) {
        int oldDaySway = mDayPickerView.getValue();

        int oldDayStop = ChineseCalendarUtil.getSumOfDayInMonth(oldYear, oldMonth, isGregorian);
        int newDayStop = ChineseCalendarUtil.getSumOfDayInMonth(newYear, newMonth, isGregorian);

        if (oldDayStop == newDayStop) {
            if (mOnDateChangedListener != null) {
                DateData dateData = getCalendarData(newYear, newMonth, oldDaySway, isGregorian);
                mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
            }
            return;//不需要更新
        } else {
            int newDaySway = oldDaySway <= newDayStop ? oldDaySway : newDayStop;
            setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, isGregorian ? mDisplayDaysGregorian : mDisplayDaysLunar, true, true);
            if (mOnDateChangedListener != null) {
                DateData dateData = getCalendarData(newYear, newMonth, newDaySway, isGregorian);
                mOnDateChangedListener.onDateChanged(dateData, isOutOfDate);
            }
            return;
        }
    }

    public void setGregorian(boolean isGregorian, boolean anim) {
        if (mIsGregorian == isGregorian) {
            return;
        }

        ChineseCalendar cc = (ChineseCalendar) getCalendarData().getCalendar();//根据mIsGregorian收集数据
        if (!checkCalendarAvailable(cc, YEAR_START, YEAR_STOP, isGregorian)) {
            cc = (ChineseCalendar) adjustCalendarByLimit(cc, YEAR_START, YEAR_STOP, isGregorian);//调整改变后的界面的显示
        }
        mIsGregorian = isGregorian;//改变mIsGregorian的数值
        setConfigs(cc, isGregorian, anim);//重新更新界面数据
    }

    private boolean checkCalendarAvailable(Calendar cc, int yearStart, int yearStop, boolean isGregorian) {
        int year = isGregorian ? cc.get(Calendar.YEAR) : ((ChineseCalendar) cc).get(ChineseCalendar.CHINESE_YEAR);
        return (yearStart <= year) && (year <= yearStop);
    }

    public View getNumberPickerYear() {
        return mYearPickerView;
    }

    public View getNumberPickerMonth() {
        return mMonthPickerView;
    }

    public View getNumberPickerDay() {
        return mDayPickerView;
    }

    public void setNumberPickerYearVisibility(int visibility) {
        setNumberPickerVisibility(mYearPickerView, visibility);
    }

    public void setNumberPickerMonthVisibility(int visibility) {
        setNumberPickerVisibility(mMonthPickerView, visibility);
    }

    public void setNumberPickerDayVisibility(int visibility) {
        setNumberPickerVisibility(mDayPickerView, visibility);
    }

    public void setNumberPickerVisibility(NumberPickerView view, int visibility) {
        if (view.getVisibility() == visibility) {
            return;
        } else if (visibility == View.GONE || visibility == View.VISIBLE || visibility == View.INVISIBLE) {
            view.setVisibility(visibility);
        }
    }

    public boolean getIsGregorian() {
        return mIsGregorian;
    }

    private boolean isOutOfDate = false;

    private DateData getCalendarData(int pickedYear, int pickedMonthSway, int pickedDay, boolean mIsGregorian) {
        if (!mIsGregorian) {
            long ymd = pickedYear * 10000 + pickedMonthSway * 100 + pickedDay;
            isOutOfDate = ymd > 21001201;
        }
        return new DateData(pickedYear, pickedMonthSway, pickedDay, mIsGregorian);
    }

    public DateData getCalendarData() {
        int pickedYear = mYearPickerView.getValue();
        int pickedMonthSway = mMonthPickerView.getValue();
        int pickedDay = mDayPickerView.getValue();
        if (!mIsGregorian) {
            long ymd = pickedYear * 10000 + pickedMonthSway * 100 + pickedDay;
            isOutOfDate = ymd > 21001201;
        }
        return new DateData(pickedYear, pickedMonthSway, pickedDay, mIsGregorian);
    }

    public static class DateData {
        public boolean isGregorian = false;
        public int pickedYear;
        public int pickedMonthSway;
        public int pickedDay;

        /**
         * 获取数据示例与说明：
         * Gregorian : //公历
         * chineseCalendar.get(Calendar.YEAR)              //获取公历年份，范围[1900 ~ 2100]
         * chineseCalendar.get(Calendar.MONTH) + 1         //获取公历月份，范围[1 ~ 12]
         * chineseCalendar.get(Calendar.DAY_OF_MONTH)      //返回公历日，范围[1 ~ 30]
         * <p>
         * Lunar
         * chineseCalendar.get(ChineseCalendar.CHINESE_YEAR)   //返回农历年份，范围[1900 ~ 2100]
         * chineseCalendar.get(ChineseCalendar.CHINESE_MONTH)) //返回农历月份，范围[(-12) ~ (-1)] || [1 ~ 12]
         * //当有月份为闰月时，返回对应负值
         * //当月份非闰月时，返回对应的月份值
         * calendar.get(ChineseCalendar.CHINESE_DATE)         //返回农历日，范围[1 ~ 30]
         */
        public ChineseCalendar chineseCalendar;

        /**
         * model类的构造方法
         *
         * @param pickedYear      年
         * @param pickedMonthSway 月，公历农历均从1开始。农历如果有闰年，按照实际的顺序添加
         * @param pickedDay       日，从1开始，日期在月份中的显示数值
         * @param isGregorian     是否为公历
         */
        public DateData(int pickedYear, int pickedMonthSway, int pickedDay, boolean isGregorian) {
            this.pickedYear = pickedYear;
            this.pickedMonthSway = pickedMonthSway;
            this.pickedDay = pickedDay;
            this.isGregorian = isGregorian;
            initChineseCalendar();
        }

        /**
         * 初始化成员变量chineseCalendar，用来记录当前选中的时间。此成员变量同时存储了农历和公历的信息
         */
        private void initChineseCalendar() {
            if (isGregorian) {
                chineseCalendar = new ChineseCalendar(pickedYear, pickedMonthSway - 1, pickedDay);//公历日期构造方法
            } else {
                int y = pickedYear;
                int m = ChineseCalendarUtil.convertMonthSwayToMonthLunarByYear(pickedMonthSway, pickedYear);
                int d = pickedDay;
                chineseCalendar = new ChineseCalendar(true, y, m, d);
            }
        }

        public Calendar getCalendar() {
            return chineseCalendar;
        }
    }

    public interface OnDateChangedListener {
        void onDateChanged(DateData dateData, boolean isOutOfDate);
    }

    public void setOnDateChangedListener(OnDateChangedListener listener) {
        mOnDateChangedListener = listener;
    }

}