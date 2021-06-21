package com.ts.base.view.timepicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ts.base.R;
import com.ts.base.util.ChineseCalendar;

import java.util.Calendar;
import java.util.Date;

/**
 * 选择日期对话框
 * Created by Joe.Wang on 2017/7/24.
 */
@SuppressWarnings("WrongConstant")
public class DatePickerDialog extends Dialog {

    public interface OnDatePickListener {
        void onDatePick(boolean isLunar, ChineseCalendar cal);
    }
    private OnDatePickListener onDatePickListener;
    public void setOnDatePickListener(OnDatePickListener l) {
        this.onDatePickListener = l;
    }

    private DatePickerView datePickerView;

    private boolean isLunar = false;
    private boolean isOutOfDate; // 选择的日期是否超出日历范围
    private Context context;
    private Calendar cal;
    private String title;

    public DatePickerDialog(@NonNull Context context) {
        this(context, Calendar.getInstance(), false);
    }

    public DatePickerDialog(@NonNull Context context, Calendar cal) {
        this(context, cal, false);
    }

    public DatePickerDialog(@NonNull Context context, Date date) {
        this(context, date, false);
    }

    public DatePickerDialog(@NonNull Context context, Calendar cal, String title) {
        this(context, cal, false);
        this.title = title;
    }

    public DatePickerDialog(@NonNull Context context, Date date, String title) {
        this(context, date, false);
        this.title = title;
    }

    private DatePickerDialog(@NonNull Context context, Calendar cal, boolean isLunar) {
        super(context, R.style.AppTheme_Dialog_DateTime);
        this.context = context;
        this.cal = cal;
        this.isLunar = isLunar;
    }

    private DatePickerDialog(@NonNull Context context, Date date, boolean isLunar) {
        super(context, R.style.AppTheme_Dialog_DateTime);
        this.context = context;
        this.cal = Calendar.getInstance();
        this.cal.setTime(date);
        this.isLunar = isLunar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_dia_date_picker);

        datePickerView = findViewById(R.id.datePickerView);
        findViewById(R.id.confirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOutOfDate) {
                } else {
                    DatePickerView.DateData dateData = datePickerView.getCalendarData();
                    ChineseCalendar cal = (ChineseCalendar) dateData.getCalendar();
                    if (onDatePickListener != null) {
                        onDatePickListener.onDatePick(isLunar, cal);
                    }
                    dismiss();
                }
            }
        });
        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView titleTv = findViewById(R.id.titleTv);
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
        }

        datePickerView.init(cal);
        datePickerView.setOnDateChangedListener(new DatePickerView.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePickerView.DateData dateData, boolean isOutOfDate) {
                DatePickerDialog.this.isOutOfDate = isOutOfDate;
            }
        });

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (isLunar) {
                    toLunarMode(false);
                }
            }
        });
    }

    private void toGregorianMode() {
        toGregorianMode(true);
    }

    private void toGregorianMode(boolean anim) {
        isLunar = false;
        datePickerView.toGregorianMode(anim);
    }

    private void toLunarMode() {
        toLunarMode(true);
    }

    private void toLunarMode(boolean anim) {
        isLunar = true;
        datePickerView.toLunarMode(anim);
    }

}