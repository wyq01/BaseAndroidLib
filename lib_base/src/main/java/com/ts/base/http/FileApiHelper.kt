package com.ts.base.http

/**
 * DEMO写法
 * 需要自行实现一个单例模式的ApiHelper
 *
 *
class ApiHelper {
companion object {
private val helper = Holder.holder

@JvmStatic
fun getInstance(): ApiHelper {
return helper
}
}

object Holder {
val holder = ApiHelper()
}

private val okHttpHelper = OkHttpHelper()
.setTag("tttsss")
.addInterceptor(Interceptor { chain ->
val newBuilder = chain.request().newBuilder()
chain.proceed(newBuilder.build())
})
private val retrofitHelper = RetrofitHelper().baseUrl("UrlConstant.baseUrl")

fun addUploadListener(uploadListener: ProgressListener?): ApiHelper {
okHttpHelper.addUploadListener(uploadListener)
return this
}

fun getApi(): ApiService {
return retrofitHelper.client(okHttpHelper.create()).getService()
}

}
 *
 */
class FileApiHelper {

    companion object {
        private val helper = Holder.holder

        @JvmStatic
        fun getInstance(): FileApiHelper {
            return helper
        }
    }

    object Holder {
        val holder = FileApiHelper()
    }

    private val okHttpHelper = OkHttpHelper()
        .setTag("tttsss")
    private val retrofitHelper = RetrofitHelper().baseUrl("http://www.baidu.com/")

    fun addUploadListener(uploadListener: ProgressListener?): FileApiHelper {
        okHttpHelper.addUploadListener(uploadListener)
        return this
    }

    fun addDownloadListener(downloadListener: ProgressListener?): FileApiHelper {
        okHttpHelper.addDownloadListener(downloadListener)
        return this
    }

    fun getApi(): FileApiService {
        return retrofitHelper.client(okHttpHelper.create()).getService()
    }

}