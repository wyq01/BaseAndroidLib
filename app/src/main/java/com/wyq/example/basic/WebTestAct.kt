package com.wyq.example.basic

import android.content.Context
import com.wyq.base.js.BaseWebActivity

/**
 * 甪直特设监管
 */
class WebTestAct : BaseWebActivity() {

    companion object {

        @JvmStatic
        fun startActivity(context: Context, url: String) {
        }
    }
    override fun url(): String {
        return "file:///android_asset/web_test.html"
    }

}