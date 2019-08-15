package com.wyq.base.util

import android.content.Context

/**
 * Created by wyq
 * Date: 2018/9/27
 */
object TokenUtil {
    const val TOKEN = "Authorization"

    fun getToken(context: Context): String {
        return SPUtil[context, TOKEN, ""] as String
    }

    fun saveToken(context: Context, token: String) {
        SPUtil.put(
            context,
            TOKEN,
            token
        )
    }

    fun clearToken(context: Context) {
        SPUtil.remove(context, TOKEN)
    }

    fun isLogin(context: Context): Boolean {
        val token = getToken(context)
        return token.isNotEmpty()
    }

}