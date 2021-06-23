package com.ts.base.http

interface ProgressListener {
    fun onStart()
    fun onProgress(progress: Long, total: Long)
    fun onSuccess(path: String)
    fun onError(error: String)
}