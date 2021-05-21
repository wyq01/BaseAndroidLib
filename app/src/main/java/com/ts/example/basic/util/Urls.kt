package com.ts.example.basic.util

import android.content.Context
import com.ts.base.util.SPUtil

object Urls {
    private const val KEY_URL = "key_url"

    fun saveUrl(context: Context, url: String) {
        val value = SPUtil[context, KEY_URL, ""] as String
        if (!value.contains(url)) {
            if (value.isEmpty()) {
                SPUtil.put(context, KEY_URL, url)
            } else {
                SPUtil.put(context, KEY_URL, "$value,$url")
            }
        }
    }

    fun getUrl(context: Context): List<String> {
        val value = SPUtil[context, KEY_URL, ""] as String
        return if (value.isEmpty()) {
            ArrayList<String>()
        } else {
            val urls = value.split(",")
            urls.toList()
        }
    }

    fun clearUrl(context: Context) {
        SPUtil.remove(context, KEY_URL)
    }

}