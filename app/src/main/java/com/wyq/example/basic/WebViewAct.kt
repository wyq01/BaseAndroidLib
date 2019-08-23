package com.wyq.example.basic

import android.content.Context
import android.content.Intent
import com.wyq.base.js.BaseWebActivity
import com.wyq.base.util.LogUtil

class WebViewAct : BaseWebActivity() {

    companion object {
        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, WebViewAct::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    private lateinit var url: String

    override fun initData(intent: Intent) {
        super.initData(intent)

        url = intent.getStringExtra("url")
        LogUtil.d("url:$url")
    }

    override fun url(): String {
        return url
    }

}