package com.wyq.base.util

import android.text.TextUtils
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

object Expand

fun String?.subString(): String {
    return subString(6)
}


fun String?.subString(count: Int): String {
    return if (this == null) {
        ""
    } else {
        if (this.length > count)
            this.substring(0, count)
        else
            convert()
    }
}

fun String?.convert(): String {
    return this ?: ""
}

fun String?.convert_(): String {
    return this ?: "-"
}

fun String?.isNull(): Boolean {
    return TextUtils.isEmpty(this)
}

fun String?.isNotNull(): Boolean {
    return !TextUtils.isEmpty(this)
}

fun View.click(action: (View) -> Unit) {
    RxView.clicks(this)
        .throttleFirst(com.wyq.base.util.ClickUtil.MIN_DELAY_TIME, TimeUnit.MILLISECONDS)
        .subscribe {
            action(this)
        }
//    RxView.clicks(view)
//        .throttleFirst(1, TimeUnit.SECONDS)
//        .subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) throws Exception {
//                // do something
//            }
//        })
}