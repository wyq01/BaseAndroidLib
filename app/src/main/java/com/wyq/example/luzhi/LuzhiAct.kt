package com.wyq.example.luzhi

import android.content.Intent
import com.wyq.base.js.BaseWebActivity
import com.wyq.base.util.LogUtil

/**
 * 甪直特设监管
 */
class LuzhiAct : BaseWebActivity() {

    companion object {
        const val URL = "http://47.103.93.231:8090/#/login"
    }

    private lateinit var url: String

    override fun initData(intent: Intent) {
        super.initData(intent)

        url = URL
        LogUtil.d("url:$url")
    }

    override fun initViews() {
        super.initViews()

    }

    override fun url(): String {
        return url
    }

}