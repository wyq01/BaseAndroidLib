package com.ts.base.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {

    var builder: Retrofit.Builder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())

    fun client(okHttpClient: OkHttpClient): RetrofitHelper {
        builder.client(okHttpClient)
        return this
    }

    fun baseUrl(baseUrl: String): RetrofitHelper {
        builder.baseUrl(baseUrl)
        return this
    }

    inline fun <reified T> getService(): T {
        return builder.build().create(T::class.java)
    }

}