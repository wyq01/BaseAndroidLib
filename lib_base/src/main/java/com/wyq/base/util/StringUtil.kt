package com.wyq.base.util

import android.text.TextUtils

/**
 * Created by wyq
 * Date: 2018/10/23
 */
object StringUtil {

    fun convert2_(str: String?): String? {
        return if (TextUtils.isEmpty(str)) "-" else str
    }

    fun convert2Null(str: String?): String? {
        return if (TextUtils.isEmpty(str)) "" else str
    }

}