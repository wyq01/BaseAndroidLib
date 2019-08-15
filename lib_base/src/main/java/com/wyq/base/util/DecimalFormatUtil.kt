package com.wyq.base.util

import java.text.DecimalFormat

object DecimalFormatUtil {
    const val offset1 = "###,###,##0.0"
    const val offset2 = "###,###,##0.00"
    const val number = "#,###"

    @JvmStatic
    fun format(value: Number): String {
        val formatter = DecimalFormat(offset1)
        return formatter.format(value)
    }

    @JvmStatic
    fun format(value: Number, pattern: String): String {
        val formatter = DecimalFormat(pattern)
        return formatter.format(value)
    }
}