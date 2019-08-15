package com.wyq.example.basic

import android.content.Context
import android.content.Intent
import com.wyq.base.js.BaseWebActivity
import com.wyq.base.util.LogUtil

/**
 * 联合执法
 */
class BasicAct : BaseWebActivity() {

    companion object {
        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, BasicAct::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private lateinit var url: String

    override fun initData(intent: Intent) {
        super.initData(intent)

        url = intent.getStringExtra("url")
        LogUtil.d("url:$url")
    }

    override fun initViews() {
        super.initViews()
    }

    override fun url(): String {
        return url
    }

}