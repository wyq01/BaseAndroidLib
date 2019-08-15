package com.wyq.base.okgo

import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.request.base.Request
import com.wyq.base.util.convert
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * json
 */
abstract class JsonCallback<T> constructor() : AbsCallback<T>() {
    private var type: Type? = null
    private var clazz: Class<T>? = null

    constructor(type: Type): this() {
        this.type = type
    }

    constructor(clazz: Class<T>): this() {
        this.clazz = clazz
    }

    override fun onStart(request: Request<T, out Request<*, *>>?) {
        super.onStart(request)
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Throws(Throwable::class)
    override fun convertResponse(response: Response): T? {

        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        //详细自定义的原理和文档，看这里： https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback

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

    open fun onError(error: String) {

    }

    open fun onCancel() {

    }

    open fun onNoNetwork() {

    }
}