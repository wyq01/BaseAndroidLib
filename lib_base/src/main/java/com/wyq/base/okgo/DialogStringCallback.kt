package com.wyq.base.okgo

import android.app.Activity
import com.wyq.base.view.ProgressDialog
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.wyq.base.util.convert
import java.net.UnknownHostException

/**
 * Created by wyq
 */
abstract class DialogStringCallback(var activity: Activity?, var cancelable: Boolean, var msg: String?) : StringCallback() {

    private var progressDialog: ProgressDialog? = null

    constructor (activity: Activity?) : this(activity, false, null)

    constructor (activity: Activity, cancelable: Boolean) : this(activity, cancelable, null)

    override fun onStart(request: Request<String, out Request<*, *>>?) {
        super.onStart(request)
        progressDialog = ProgressDialog.showDialog(activity, msg, cancelable)
        if (cancelable) {
            progressDialog?.setOnDismissListener { onCancel() }
        }
    }

    override fun onFinish() {
        super.onFinish()
        progressDialog?.setOnDismissListener(null)
        ProgressDialog.hideDialog()
    }

    override fun onError(response: Response<String>) {
        super.onError(response)
        if (response.exception is UnknownHostException) {
            onNoNetwork()
            return
        }
        onError(response.exception.message.convert())
    }

    open fun onError(error: String) {

    }

    open fun onCancel() {

    }

    open fun onNoNetwork() {

    }
}