package com.wyq.base.okgo

import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.wyq.base.util.convert
import java.net.UnknownHostException

/**
 * Created by wyq
 */
abstract class BaseStringCallback: StringCallback() {

    override fun onError(response: Response<String>) {
        super.onError(response)
        if (response.exception is UnknownHostException) {
            onNoNetwork()
            return
        }
        onError(response.exception.message.convert())
    }

    private fun onNoNetwork() {

    }

    open fun onError(error: String) {

    }

    open fun onCancel() {

    }

}