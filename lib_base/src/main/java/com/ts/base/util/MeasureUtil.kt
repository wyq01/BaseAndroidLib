package com.ts.base.util

import android.view.View

object MeasureUtil {

    @JvmStatic
    fun measureWidthOrHeight(measureSpec: Int): Int {
        var result = 0
        //获取当前View的测量模式
        val mode = View.MeasureSpec.getMode(measureSpec)
        //精准模式获取当前Viwe测量后的值,如果是最大值模式,会获取父View的大小.
        val size = View.MeasureSpec.getSize(measureSpec)
        if (mode == View.MeasureSpec.EXACTLY) {
            //当测量模式为精准模式,返回设定的值
            result = size
        } else {
            //设置为WrapContent的默认大小
            result = 200
            if (mode == View.MeasureSpec.AT_MOST) {
                //当模式为最大值的时候,默认大小和父类View的大小进行对比,返回最小的值
                result = Math.min(result, size)
            }
        }
        return result
    }
}