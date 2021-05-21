package com.ts.base.okgo

import com.google.gson.stream.JsonReader
import com.lzy.okgo.convert.Converter
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by ts
 */
class JsonConvert<T>() : Converter<T> {

    var type: Type? = null
    var clazz: Class<T>? = null

    constructor(type: Type?): this() {
        this.type = type
    }

    constructor(clazz: Class<T>?): this() {
        this.clazz = clazz
    }

    override fun convertResponse(response: Response?): T? {
        if (type == null) {
            if (clazz == null) {
                // 如果没有通过构造函数传进来，就自动解析父类泛型的真实类型（有局限性，继承后就无法解析到）
                val genType = javaClass.genericSuperclass
                type = (genType as ParameterizedType).actualTypeArguments[0]
            } else {
                return parseClass(response, clazz)
            }
        }

        return when (type) {
            is ParameterizedType -> parseParameterizedType(response, type as ParameterizedType)
            is Class<*> -> parseClass(response, type as Class<*>)
            else -> parseType(response, type)
        }
    }

    @Throws(Exception::class)
    private fun parseClass(response: Response?, rawType: Class<*>?): T? {
        if (rawType == null) return null
        val body = response?.body() ?: return null
        val jsonReader = JsonReader(body.charStream())

        return when (rawType) {
            String::class.java -> body.string() as T
            JSONObject::class.java -> JSONObject(body.string()) as T
            JSONArray::class.java -> JSONArray(body.string()) as T
            else -> {
                val t = Convert.fromJson<T>(jsonReader, rawType)
                response.close()
                t
            }
        }
    }

    @Throws(Exception::class)
    private fun parseType(response: Response?, type: Type?): T? {
        if (type == null) return null
        val body = response?.body() ?: return null
        val jsonReader = JsonReader(body.charStream())

        // 泛型格式如下： new JsonCallback<任意JavaBean>(this)
        val t = Convert.fromJson<T>(jsonReader, type)
        response.close()
        return t
    }

    @Throws(Exception::class)
    private fun parseParameterizedType(response: Response?, type: ParameterizedType?): T? {
        if (type == null) return null
        val body = response?.body() ?: return null
        val jsonReader = JsonReader(body.charStream())

        val rawType = type.rawType                     // 泛型的实际类型
        val typeArgument = type.actualTypeArguments[0] // 泛型的参数
        if (rawType != ResultBean::class.java) {
            // 泛型格式如下： new JsonCallback<外层BaseBean<内层JavaBean>>(this)
            val t = Convert.fromJson<T>(jsonReader, type)
            response.close()
            return t
        } else {
            val lzyResponse =
                Convert.fromJson<ResultBean<*>>(
                    jsonReader,
                    type
                )
            response.close()
            val code = lzyResponse.resultCode
            when (code) {
                1 -> return lzyResponse as T
                500 -> throw IllegalStateException(lzyResponse.resultCode.toString())
                else -> {
//                    ToastStaticUtil.showToast(lzyResponse.getMessage());
//                    return (T) lzyResponse;
                    throw IllegalStateException(lzyResponse.errMsg)
                }
            }
            //                int code = lzyResponse.getRetCode();
            //                //这里的0是以下意思
            //                //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
            //                if (code == 0) {
            //                    //noinspection unchecked
            //                    return (T) lzyResponse;
            //                } else if (code == 104) {
            //                    throw new IllegalStateException("用户授权信息无效");
            //                } else if (code == 105) {
            //                    throw new IllegalStateException("用户收取信息已过期");
            //                } else {
            //                    //直接将服务端的错误信息抛出，onError中可以获取
            //                    throw new IllegalStateException("错误代码：" + code + "，错误信息：" + lzyResponse.getMsg());
            //                }
            //            }
        }
    }
}