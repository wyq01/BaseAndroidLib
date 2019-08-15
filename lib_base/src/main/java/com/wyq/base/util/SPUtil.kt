package com.wyq.base.util

import android.content.Context
import android.content.SharedPreferences

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * SharedPreferences统一管理类
 */
object SPUtil {
    /**
     * 保存在手机里面的文件名
     */
    private const val FILE_NAME = "tech_service"

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param obj
     */
    fun put(context: Context?, key: String, obj: Any) {
        if (context == null) {
            return
        }
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()

        when (obj) {
            is String -> editor.putString(key, obj)
            is Int -> editor.putInt(key, obj)
            is Boolean -> editor.putBoolean(key, obj)
            is Float -> editor.putFloat(key, obj)
            is Long -> editor.putLong(key, obj)
            else -> editor.putString(key, obj.toString())
        }
        SPUtil.SharedPreferencesCompat.apply(editor)
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObj
     * @return
     */
    operator fun get(context: Context?, key: String, defaultObj: Any): Any? {
        if (context == null) {
            return null
        }
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

        return when (defaultObj) {
            is String -> sp.getString(key, defaultObj)
            is Int -> sp.getInt(key, defaultObj)
            is Boolean -> sp.getBoolean(key, defaultObj)
            is Float -> sp.getFloat(key, defaultObj)
            is Long -> sp.getLong(key, defaultObj)
            else -> null
        }
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    fun remove(context: Context?, key: String) {
        if (context == null) {
            return
        }
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove(key)
        SPUtil.SharedPreferencesCompat.apply(editor)
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    fun clear(context: Context?) {
        if (context == null) {
            return
        }
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        SPUtil.SharedPreferencesCompat.apply(editor)
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    fun contains(context: Context, key: String): Boolean {
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sp.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    fun getAll(context: Context): Map<String, *> {
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sp.all
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private object SharedPreferencesCompat {
        private val sApplyMethod =
            findApplyMethod()
        /**
         * 反射查找apply的方法
         * @return
         */
        private fun findApplyMethod(): Method? {
            try {
                val clz = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
            }

            return null
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         * @param editor
         */
        fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: IllegalArgumentException) {
            } catch (e: IllegalAccessException) {
            } catch (e: InvocationTargetException) {
            }
            editor.commit()
        }
    }
}