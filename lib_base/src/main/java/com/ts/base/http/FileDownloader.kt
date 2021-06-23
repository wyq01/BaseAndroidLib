package com.ts.base.http

import android.content.Context
import android.os.Environment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.*

/**
 * 文件下载器
 */
object FileDownloader {

    fun download(context: Context, url: String, targetFile: File? = null, progressListener: ProgressListener? = null) {
        val fileName = url.substring(url.lastIndexOf('/') + 1)
        FileApiHelper.getInstance()
            .addDownloadListener(progressListener)
            .getApi()
            .download(url)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation()) // 用于计算任务
            .map { responseBody ->
                write2File(context, fileName, responseBody, targetFile)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<File> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {
                    progressListener?.onStart()
                }
                override fun onNext(t: File) {
                    progressListener?.onSuccess(t.path)
                }
                override fun onError(e: Throwable) {
                    progressListener?.onError(e.message ?: "未知错误")
                }
            })
    }

    private fun write2File(context: Context, fileName: String, body: ResponseBody, targetFile: File? = null): File {
        val file = targetFile ?: File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        var inputStream: InputStream? = null
        val buf = ByteArray(4096)
        var len: Int
        var fos: FileOutputStream? = null
        try {
            inputStream = body.byteStream()
            fos = FileOutputStream(file)
            while (inputStream.read(buf).also { len = it } != -1) {
                fos.write(buf, 0, len)
            }
            fos.flush()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

}