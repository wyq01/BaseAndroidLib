package com.ts.base.http

import android.content.Context
import android.content.DialogInterface
import com.blankj.utilcode.util.ToastUtils
import com.ts.base.view.BaseProgressDialog
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.net.UnknownHostException

/**
 * DEMO写法
 * 需要自行实现
 */
abstract class IObserver<T>(
    var context: Context,
    var showDialog: Boolean = false,
    var msg: String = "",
    var cancelable: Boolean = true
) : Observer<IResultBean<T>> {

    companion object {
        const val CODE_SUCCESS = 0 // 请求码-正确返回
        const val CODE_INVALID_TOKEN = 1001 // 请求码-token过期
    }

    override fun onSubscribe(d: Disposable) {
        if (showDialog) {
            BaseProgressDialog.showDialog(context, msg, cancelable,
                DialogInterface.OnDismissListener {
                    d.dispose()
                    onCancel()
                })
        }
        onStart()
    }

    override fun onNext(t: IResultBean<T>) {
        when (t.errcode) {
            CODE_SUCCESS -> if (t.data == null) onError("接口返回数据不正确") else onSuccess(t.data)
            CODE_INVALID_TOKEN -> onInvalidToken()
            else -> onError(t.msg)
        }
    }

    override fun onError(e: Throwable) {
        BaseProgressDialog.hideDialog()
        if (e is UnknownHostException) {
            onNetworkError()
        } else {
            onError(e.message ?: "未知错误")
        }
        onFinish()
    }

    override fun onComplete() {
        BaseProgressDialog.hideDialog()
        onFinish()
    }

    open fun onStart() {}

    abstract fun onSuccess(t: T)

    open fun onError(error: String) {
        ToastUtils.showShort(error)
    }

    open fun onInvalidToken() {}

    open fun onFinish() {}

    open fun onCancel() {}

    open fun onNetworkError() {}
}