package com.wyq.base.util

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.wyq.base.BuildConfig

/**
 * log类
 */
object LoggerUtil {
    const val TAG = "tech_service"

    init {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
            .tag(TAG)   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    @JvmStatic
    fun i(obj: Any) {
        Logger.i(obj.toString())
    }

    @JvmStatic
    fun d(obj: Any) {
        Logger.d(obj.toString())
    }

    @JvmStatic
    fun e(obj: Any) {
        Logger.e(obj.toString())
    }

    @JvmStatic
    fun v(obj: Any) {
        Logger.v(obj.toString())
    }

    @JvmStatic
    fun w(obj: Any) {
        Logger.w(obj.toString())
    }

    @JvmStatic
    fun json(json: String) {
        Logger.json(json)
    }

}