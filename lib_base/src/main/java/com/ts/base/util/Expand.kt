package com.ts.base.util

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.lifecycle.LifecycleOwner
import cn.pedant.SafeWebViewBridge.JsCallback
import com.jakewharton.rxbinding2.view.RxView
import com.ts.base.lifecycle.DisposableLifecycleObserver
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.SoftReference
import java.util.concurrent.TimeUnit

object Expand

fun String?.subString(): String {
    return subString(6)
}

fun String?.subString(count: Int): String {
    return if (this == null) {
        ""
    } else {
        if (this.length > count)
            this.substring(0, count)
        else
            convert()
    }
}

fun String?.convert(): String {
    return this ?: ""
}

fun String?.convert_(): String {
    return this ?: "-"
}

fun String?.isNull(): Boolean {
    return TextUtils.isEmpty(this)
}

fun String?.isNotNull(): Boolean {
    return !TextUtils.isEmpty(this)
}

@SuppressLint("CheckResult")
fun View.click(action: (View) -> Unit) {
    RxView.clicks(this)
        .throttleFirst(ClickUtil.MIN_DELAY_TIME, TimeUnit.MILLISECONDS)
        .subscribe {
            action(this)
        }
}

// 线程切换
fun Completable.back2Main(): Completable {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}
fun <T> Single<T>.back2Main(): Single<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}
fun <T> Flowable<T>.back2Main(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}
fun <T> Maybe<T>.back2Main(): Maybe<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}
// 绑定生命周期
fun Disposable.bindLifecycle(lifecycleOwner: LifecycleOwner): Disposable {
    lifecycleOwner.lifecycle.addObserver(DisposableLifecycleObserver(this))
    return this
}

@Throws
fun JsCallback.applyJson(json: String) {
    val clazz = this.javaClass
    val callbackJsFormatField = clazz.getDeclaredField("CALLBACK_JS_FORMAT")
    callbackJsFormatField.isAccessible = true
    val CALLBACK_JS_FORMAT = callbackJsFormatField.get(this) as String

    val mIndexField = clazz.getDeclaredField("mIndex")
    mIndexField.isAccessible = true
    val mIndex = mIndexField.getInt(this)

    val mCouldGoOnField = clazz.getDeclaredField("mCouldGoOn")
    mCouldGoOnField.isAccessible = true
    val mCouldGoOn = mCouldGoOnField.getBoolean(this)

    val mWebViewRefField = clazz.getDeclaredField("mWebViewRef")
    mWebViewRefField.isAccessible = true
    val mWebViewRef = mWebViewRefField.get(this) as SoftReference<WebView>

    val mIsPermanentField = clazz.getDeclaredField("mIsPermanent")
    mIsPermanentField.isAccessible = true
    val mIsPermanent = mIsPermanentField.getInt(this)

    val mInjectedNameField = clazz.getDeclaredField("mInjectedName")
    mInjectedNameField.isAccessible = true
    val mInjectedName = mInjectedNameField.get(this) as String

    if (mWebViewRef.get() == null) {
        throw JsCallback.JsCallbackException("the WebView related to the JsCallback has been recycled")
    }

    if (!mCouldGoOn) {
        throw JsCallback.JsCallbackException("the JsCallback isn't permanent,cannot be called more than once")
    }
    val sb = StringBuilder()
    sb.append(",")
    sb.append("'")
    sb.append(json)
    sb.append("'")

    val execJs = String.format(CALLBACK_JS_FORMAT, mInjectedName, mIndex, mIsPermanent, sb.toString())
    Log.d("JsCallBack", execJs)
    mWebViewRef.get()?.loadUrl(execJs)
    mCouldGoOnField.set(this, mIsPermanent > 0)
}