package com.wyq.base.util

import android.content.Context
import android.support.annotation.DrawableRes
import android.text.TextUtils
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener

/**
 * Created by wyq
 * Date: 2018/10/29
 */
object ImageLoader {

    private const val URL_CONDITION = "http"

    fun load(context: Context, url: String?, @DrawableRes drawableId: Int, target: ImageView) {
        url?.let {
            if (TextUtils.isEmpty(it)) {
                return
            }
            val imgUrl = convertUrl(it)
            Glide.with(context)
                .load(imgUrl)
                .placeholder(drawableId)
                .error(drawableId)
                .into(target)
        }
    }

    fun load(context: Context, url: String?, target: ImageView) {
        url?.let {
            if (TextUtils.isEmpty(it)) {
                return
            }
            val imgUrl = convertUrl(it)
//            LogUtil.d("加载的图片 $imgUrl")
            Glide.with(context)
                .load(imgUrl)
                .into(target)
        }
    }

    fun load(context: Context, url: String?, target: ImageView, resId: Int) {
        url?.let {
            if (TextUtils.isEmpty(it)) {
                return
            }
            val imgUrl = convertUrl(it)
//            LogUtil.d("加载的图片 $imgUrl")
            Glide.with(context)
                .load(imgUrl)
                .placeholder(resId)
                .error(resId)
                .into(target)
        }
    }

    fun load(context: Context, url: String?, target: ImageView, l: RequestListener<String, GlideDrawable>) {
        url?.let {
            if (TextUtils.isEmpty(it)) {
                return
            }
            val imgUrl = convertUrl(it)
            Glide.with(context)
                .load(imgUrl)
                .listener(l)
                .into(target)
        }
    }

    fun loadCenterCrop(context: Context, url: String?, target: ImageView) {
        url?.let {
            if (TextUtils.isEmpty(it)) {
                return
            }
            val imgUrl = convertUrl(it)
            Glide.with(context)
                .load(imgUrl)
                .centerCrop()
                .into(target)
        }
    }

    private fun convertUrl(url: String): String {
        return url
    }

}