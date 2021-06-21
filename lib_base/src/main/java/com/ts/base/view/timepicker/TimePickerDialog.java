package com.ts.base.view.timepicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ts.base.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 选择日期对话框
 * Created by Joe.Wang on 2017/7/24.
 */
@SuppressWarnings("WrongConstant")
public class TimePickerDialog extends Dialog {

    public interface OnTimePickListener {
        void onTimePick(int hour, int minute);
    }
    private OnTimePickListener onTimePickListener;
    public void setOnTimePickListener(OnTimePickListener l) {
        this.onTimePickListener = l;
    }

    TimePickerView pickerView;

    private Calendar cal;
    private String title;

    public TimePickerDialog(@NonNull Context context, Calendar cal) {
        super(context, R.style.AppTheme_Dialog_DateTime);
        this.cal = cal;
    }

    public TimePickerDialog(@NonNull Context context, Calendar cal, String title) {
        super(context, R.style.AppTheme_Dialog_DateTime);
        this.title = title;
    }

    public TimePickerDialog(@NonNull Context context, Date date, String title) {
        super(context, R.style.AppTheme_Dialog_DateTime);
        this.title = title;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_dia_time_picker);

        pickerView = findViewById(R.id.timePickerView);
        findViewById(R.id.confirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView.TimeData timeData = pickerView.getTimeData();
                if (onTimePickListener != null) {
                    onTimePickListener.onTimePick(timeData.getHour(), timeData.getMinute());
                }
                dismiss();
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

        pickerView.init(cal);
    }

}