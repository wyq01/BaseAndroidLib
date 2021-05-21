package com.ts.example.basic

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.ts.base.js.BaseWebActivity

class WebViewAct : BaseWebActivity() {

    companion object {
        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, WebViewAct::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    private lateinit var url: String

    override fun initViews() {
        super.initViews()

        mWebView?.setBackgroundColor(0) // 设置背景色
        mWebView?.background?.alpha = 0
    }

    override fun initData(intent: Intent) {
        super.initData(intent)

        url = intent.getStringExtra("url")
        LogUtils.d("url:$url")
    }

    override fun url(): String {
        return url
    }

}