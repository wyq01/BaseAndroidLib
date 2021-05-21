package com.ts.base.util

import android.content.Context
import android.support.annotation.DrawableRes
import android.text.TextUtils
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener

/**
 * Created by ts
 * Date: 2018/10/29
 */
object ImageLoader {

    fun load(context: Context, url: String?, @DrawableRes drawableId: Int, target: ImageView) {
        url?.let {
            if (TextUtils.isEmpty(it)) {
                return
            }
            Glide.with(context)
                .load(it)
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
            Glide.with(context)
                .load(it)
                .into(target)
        }
    }

    fun load(context: Context, url: String?, target: ImageView, resId: Int) {
        url?.let {
            if (TextUtils.isEmpty(it)) {
                return
            }
            Glide.with(context)
                .load(it)
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
            Glide.with(context)
                .load(it)
                .listener(l)
                .into(target)
        }
    }

    fun loadCenterCrop(context: Context, url: String?, target: ImageView) {
        url?.let {
            if (TextUtils.isEmpty(it)) {
                return
            }
            Glide.with(context)
                .load(it)
                .centerCrop()
                .into(target)
        }
    }

}