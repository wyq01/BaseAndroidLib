package com.wyq.base.util

import android.util.Log

import com.wyq.base.BuildConfig

/**
 * log类
 */
object LogUtil {

    @JvmStatic
    fun i(obj: Any) {
        if (BuildConfig.DEBUG) {
            Log.i(LoggerUtil.TAG, obj.toString())
        }
    }

    @JvmStatic
    fun d(obj: Any) {
        if (BuildConfig.DEBUG) {
            Log.d(LoggerUtil.TAG, obj.toString())
        }
    }

    @JvmStatic
    fun e(obj: Any) {
        if (BuildConfig.DEBUG) {
            Log.e(LoggerUtil.TAG, obj.toString())
        }
    }

    @JvmStatic
    fun v(obj: Any) {
        if (BuildConfig.DEBUG) {
            Log.v(LoggerUtil.TAG, obj.toString())
        }
    }

    @JvmStatic
    fun w(obj: Any) {
        if (BuildConfig.DEBUG) {
            Log.w(LoggerUtil.TAG, obj.toString())
        }
    }

}
