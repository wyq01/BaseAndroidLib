package com.ts.base.okgo

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import java.util.*

/**
 * Created by ts
 */
class JsonConverterUtil {
    companion object {
        private val gson by lazy<Gson> {
            GsonBuilder()
                    .setPrettyPrinting()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapter(Date::class.java, DateTypeAdapter())
                    .create()
        }

        fun getInstance(): Gson {
            return gson
        }
    }
}