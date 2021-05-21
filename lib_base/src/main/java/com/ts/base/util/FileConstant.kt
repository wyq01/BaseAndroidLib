package com.ts.base.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore

import java.io.File
import java.net.URISyntaxException

/**
 * Created by ts
 * Date: 2018/10/19
 */
object FileConstant {
    val MAX_SIZE = 100 * 1024

    val TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory().path + File.separator + "temp.jpg"

    @Throws(URISyntaxException::class)
    fun getPath(context: Context, uri: Uri?): String? {
        uri?.let {
            when {
                "content".equals(uri.scheme, ignoreCase = true) -> {
                    val projection = arrayOf(MediaStore.Images.Media.DATA)
                    var cursor: Cursor? = null
                    try {
                        cursor = context.contentResolver.query(uri, projection, null, null, null)
                    } catch (e: Exception) {
                        // Eat it  Or Log it.
                    }
                    cursor?.let {
                        val columnIndex = it.getColumnIndexOrThrow("_data")
                        if (it.moveToFirst()) {
                            return it.getString(columnIndex)
                        }
                    }
                }
                "file".equals(uri.scheme, ignoreCase = true) -> return uri.path
                else -> return null
            }
        }
        return null
    }
}