package com.ts.base.util

import android.content.Context
import android.support.annotation.DimenRes

object DensityUtil {

    @JvmStatic
    fun dipToSp(context: Context?, f: Float): Int {
        return (context!!.resources.displayMetrics.density * f + 0.5f).toInt()
    }

    @JvmStatic
    fun spToDip(context: Context?, f: Float): Int {
        return (context!!.resources.displayMetrics.scaledDensity * f + 0.5f).toInt()
    }

    @JvmStatic
    fun dpToPx(context: Context?, @DimenRes resId: Int): Int {
        return context!!.resources.getDimensionPixelOffset(resId)
    }

    @JvmStatic
    fun dpToPxFloat(context: Context?, @DimenRes resId: Int): Float {
        return context!!.resources.getDimension(resId)
    }

}