package com.ts.base.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import com.ts.base.R;

/**
 * 系统自带对话框，并改变按钮颜色
 * Created by Joe.Wang on 2017/8/4.
 */
public class BaseDialog {

    public static class Builder {
        private Context context;
        private AlertDialog.Builder builder;

        public Builder(Context context) {
            this.context = context;
            this.builder = new AlertDialog.Builder(context);
        }

        public Builder setTitle(@StringRes int titleId) {
            builder.setTitle(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            builder.setTitle(title);
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            builder.setMessage(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            builder.setMessage(message);
            return this;
        }

        public Builder setPositiveButton(@StringRes int textId, DialogInterface.OnClickListener listener) {
            builder.setPositiveButton(textId, listener);
            return this;
        }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            builder.setPositiveButton(text, listener);
            return this;
        }

        public Builder setNegativeButton(@StringRes int textId, DialogInterface.OnClickListener listener) {
            builder.setNegativeButton(textId, listener);
            return this;
        }

        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            builder.setNegativeButton(text, listener);
            return this;
        }

        public Builder setNeutralButton(@StringRes int textId, DialogInterface.OnClickListener listener) {
            builder.setNeutralButton(textId, listener);
            return this;
        }

        public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            builder.setNeutralButton(text, listener);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            builder.setCancelable(cancelable);
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            builder.setOnCancelListener(onCancelListener);
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            builder.setOnDismissListener(onDismissListener);
            return this;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            builder.setOnKeyListener(onKeyListener);
            return this;
        }

        public Builder setItems(@ArrayRes int itemsId, DialogInterface.OnClickListener listener) {
            builder.setItems(itemsId, listener);
            return this;
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
            builder.setItems(items, listener);
            return this;
        }

        public Builder setAdapter(final ListAdapter adapter, DialogInterface.OnClickListener listener) {
            builder.setAdapter(adapter, listener);
            return this;
        }

        public Builder setMultiChoiceItems(@ArrayRes int itemsId, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
            builder.setMultiChoiceItems(itemsId, checkedItems, listener);
            return this;
        }

        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
            builder.setMultiChoiceItems(items, checkedItems, listener);
            return this;
        }

        public Builder setSingleChoiceItems(@ArrayRes int itemsId, int checkedItem, DialogInterface.OnClickListener listener) {
            builder.setSingleChoiceItems(itemsId, checkedItem, listener);
            return this;
        }

        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener) {
            builder.setSingleChoiceItems(items, checkedItem, listener);
            return this;
        }

        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, DialogInterface.OnClickListener listener) {
            builder.setSingleChoiceItems(adapter, checkedItem, listener);
            return this;
        }

        public Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
            builder.setOnItemSelectedListener(listener);
            return this;
        }

        public Builder setView(int layoutResId) {
            builder.setView(layoutResId);
            return this;
        }

        public Builder setView(View view) {
            builder.setView(view);
            return this;
        }

        public Builder create() {
            return this;
        }

        /**
         * 在show之后改变按钮的颜色
         */
        public AlertDialog show() {
            final AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (positiveBtn != null) {
                positiveBtn.setTextColor(context.getResources().getColor(R.color.base_theme));
            }
            Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            if (negativeBtn != null) {
                negativeBtn.setTextColor(context.getResources().getColor(R.color.base_theme));
            }
            Button neutralBtn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            if (neutralBtn != null) {
                neutralBtn.setTextColor(context.getResources().getColor(R.color.base_theme));
            }
            return dialog;
        }
    }
}
