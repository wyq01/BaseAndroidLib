package com.wyq.example.union

import android.content.Intent
import com.wyq.base.js.BaseWebActivity
import com.wyq.base.util.LogUtil

/**
 * 联合执法
 */
class UnionAloneAct : BaseWebActivity() {

    companion object {
        const val URL = "http://47.103.59.229:8080/#/"
    }

    private lateinit var url: String

    override fun initData(intent: Intent) {
        super.initData(intent)

        url = URL
        LogUtil.d("url:$url")
    }

    override fun url(): String {
        return url
    }

}