package com.wyq.base.okgo

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.Reader
import java.lang.reflect.Type

/**
 * Created by wyq
 */
class Convert {

    companion object {
        class GsonHolder {
            companion object {
                var gson = Gson()
            }
        }

        private fun create(): Gson {
            return Companion.GsonHolder.gson
        }

        @Throws(JsonIOException::class, JsonSyntaxException::class)
        fun <T> fromJson(json: String, type: Class<T>): T {
            return create().fromJson(json, type)
        }

        fun <T> fromJson(json: String, type: Type): T {
            return create().fromJson(json, type)
        }

        @Throws(JsonIOException::class, JsonSyntaxException::class)
        fun <T> fromJson(reader: JsonReader, typeOfT: Type): T {
            return create().fromJson(reader, typeOfT)
        }

        @Throws(JsonSyntaxException::class, JsonIOException::class)
        fun <T> fromJson(json: Reader, classOfT: Class<T>): T {
            return create().fromJson(json, classOfT)
        }

        @Throws(JsonIOException::class, JsonSyntaxException::class)
        fun <T> fromJson(json: Reader, typeOfT: Type): T {
            return create().fromJson(json, typeOfT)
        }

        fun toJson(src: Any): String {
            return create().toJson(src)
        }

        fun toJson(src: Any, typeOfSrc: Type): String {
            return create().toJson(src, typeOfSrc)
        }

        fun formatJson(json: String): String {
            return try {
                val jp = JsonParser()
                val je = jp.parse(json)
                JsonConverterUtil.getInstance().toJson(je)
            } catch (e: Exception) {
                json
            }
        }

        fun formatJson(src: Any): String? {
            return try {
                val jp = JsonParser()
                val je = jp.parse(toJson(src))
                JsonConverterUtil.getInstance().toJson(je)
            } catch (e: Exception) {
                e.message
            }

        }
    }
}