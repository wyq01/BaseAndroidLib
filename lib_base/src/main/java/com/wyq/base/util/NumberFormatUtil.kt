package com.wyq.base.util

import java.math.BigDecimal

object NumberFormatUtil {

    fun format(any: Any): Float {
        return format(any, 2)
    }

    fun format(any: Any, scale: Int): Float {
        val num = when (any) {
            is Float -> BigDecimal(any.toDouble())
            is Double -> BigDecimal(any)
            else -> BigDecimal(0)
        }
        return num.setScale(scale, BigDecimal.ROUND_HALF_UP).toFloat()
    }
}