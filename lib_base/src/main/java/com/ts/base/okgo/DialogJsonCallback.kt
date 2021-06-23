package com.ts.base.okgo

import android.app.Activity
import com.ts.base.view.BaseProgressDialog
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.request.base.Request
import com.ts.base.util.convert
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by ts
 */
abstract class DialogJsonCallback<T>(var activity: Activity?, var cancelable: Boolean, var msg: String) : AbsCallback<T>() {
    constructor(activity: Activity): this(activity, false, "")
    constructor(activity: Activity, cancelable: Boolean): this(activity, cancelable, "")

    private var type: Type? = null
    private var clazz: Class<T>? = null
    private var baseProgressDialog: BaseProgressDialog? = null

    override fun onStart(request: Request<T, out Request<Any, Request<*, *>>>?) {
        super.onStart(request)
        baseProgressDialog = BaseProgressDialog.showDialog(activity, msg, cancelable)
        if (baseProgressDialog != null && cancelable) {
            baseProgressDialog?.setOnDismissListener {
                onCancel()
            }
        }
    }

    override fun onFinish() {
        super.onFinish()
        baseProgressDialog?.setOnDismissListener(null)
        BaseProgressDialog.hideDialog()
    }

    override fun convertResponse(response: Response): T? {
        if (type == null) {
            if (clazz == null) {
                val genType = javaClass.genericSuperclass
                type = (genType as ParameterizedType).actualTypeArguments[0]
            } else {
                val convert = JsonConvert(clazz)
                return convert.convertResponse(response)
            }
        }

        val convert = JsonConvert<T>(type)
        return convert.convertResponse(response)
    }

    override fun onError(response: com.lzy.okgo.model.Response<T>) {
        super.onError(response)
//        if (response.getException().equals("500")) {
//            ToastUtil.shortToast(Utils.getApp(), "登录信息已过期，请重新登录");
//            ActivityUtils.startActivity(LoginActivity.class);
//            EventBus.getDefault().post(new BaseEvent ());
//        } else if (response.getException() instanceof UnknownHostException) {
        onNoNetwork()
//            return;
//        }
        onError(response.exception.message.convert())
    }

    open fun onNoNetwork() {

    }

    open fun onError(error: String) {

    }

    open fun onCancel() {

    }

}