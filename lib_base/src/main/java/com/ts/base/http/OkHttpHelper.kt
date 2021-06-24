package com.ts.base.http

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.LogUtils
import com.ts.base.util.IOUtil
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.*
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

class OkHttpHelper {

    companion object {
        private const val timeout = 45 * 1000L // 超时时间

        const val WHAT_PROGRESS_UPLOAD = 1
        const val WHAT_PROGRESS_DOWNLOAD = 2

        const val DATA_PROGRESS = "progress"
        const val DATA_TOTAL = "total"
    }

    private var uploadListener: ProgressListener? = null
    private var downloadListener: ProgressListener? = null

    private val progressHandler = Handler(Looper.getMainLooper()) {
        val progress = it.data.getLong(DATA_PROGRESS)
        val total = it.data.getLong(DATA_TOTAL)
        when (it.what) {
            WHAT_PROGRESS_UPLOAD -> {
                uploadListener?.onProgress(progress, total)
            }
            WHAT_PROGRESS_DOWNLOAD -> {
                downloadListener?.onProgress(progress, total)
            }
        }
        true
    }

    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .readTimeout(timeout, TimeUnit.MILLISECONDS)
        .writeTimeout(timeout, TimeUnit.MILLISECONDS)
        .connectTimeout(timeout, TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(false)
        // 上传进度
        .addNetworkInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
            original.body()?.let {
                builder.method(original.method(), ProgressRequestBody(progressHandler, it))
            }
            chain.proceed(builder.build())
        }
        // 下载进度
        .addNetworkInterceptor { chain ->
            val original = chain.proceed(chain.request())
            val builder = original.newBuilder()
            original.body()?.let {
                builder.body(ProgressResponseBody(progressHandler, it))
            }
            builder.build()
        }

    fun setTag(tag: String): OkHttpHelper {
        val logInterceptor = LogInterceptor(tag)
            .setPrintLevel(LogInterceptor.Level.BODY)
            .setColorLevel(Level.INFO)
        builder.addInterceptor(logInterceptor)
        return this
    }

    fun addUploadListener(uploadListener: ProgressListener?): OkHttpHelper {
        this.uploadListener = uploadListener
        return this
    }

    fun addDownloadListener(downloadListener: ProgressListener?): OkHttpHelper {
        this.downloadListener = downloadListener
        return this
    }

    fun addInterceptor(interceptor: Interceptor): OkHttpHelper {
        builder.addInterceptor(interceptor)
        return this
    }

    fun build(): OkHttpClient.Builder {
        return builder
    }

    fun create(): OkHttpClient {
        return builder.build()
    }

    private class ProgressRequestBody internal constructor(var progressHandler: Handler, var requestBody: RequestBody) : RequestBody() {
        override fun contentType(): MediaType? {
            return requestBody.contentType()
        }
        @Throws(IOException::class)
        override fun contentLength(): Long {
            return requestBody.contentLength()
        }
        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val bufferedSink = Okio.buffer(sink(sink))
            requestBody.writeTo(bufferedSink)
            bufferedSink.flush()
        }
        private fun sink(sink: Sink): Sink {
            return object : ForwardingSink(sink) {
                var bytesWritten = 0L
                @Throws(IOException::class)
                override fun write(source: Buffer?, byteCount: Long) {
                    super.write(source!!, byteCount)
                    bytesWritten += byteCount
                    val contentLength = contentLength()
                    LogUtils.d("上传进度：$bytesWritten:$contentLength")

                    val message = progressHandler.obtainMessage()
                    message.what = WHAT_PROGRESS_UPLOAD

                    val data = Bundle()
                    data.putLong(DATA_PROGRESS, bytesWritten)
                    data.putLong(DATA_TOTAL, contentLength)

                    message.data = data
                    progressHandler.sendMessage(message)
                }
            }
        }
    }

    private class ProgressResponseBody internal constructor(var progressHandler: Handler, var responseBody: ResponseBody) : ResponseBody() {
        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }
        override fun contentLength(): Long {
            return responseBody.contentLength()
        }
        override fun source(): BufferedSource {
            return Okio.buffer(source(responseBody.source()))
        }
        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                var totalBytesRead = 0L
                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)

                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    val contentLength = contentLength()

                    LogUtils.d("下载进度：$totalBytesRead:$contentLength")

                    val message = progressHandler.obtainMessage()
                    message.what = WHAT_PROGRESS_DOWNLOAD

                    val data = Bundle()
                    data.putLong(DATA_PROGRESS, totalBytesRead)
                    data.putLong(DATA_TOTAL, contentLength)

                    message.data = data
                    progressHandler.sendMessage(message)

                    return bytesRead
                }
            }
        }
    }

    class LogInterceptor(tag: String) : Interceptor {
        private var printLevel: Level? = Level.NONE
        private var colorLevel: java.util.logging.Level? = null
        private val logger: Logger = Logger.getLogger(tag)

        enum class Level {
            NONE,    // 不打印log
            BASIC,   // 只打印 请求首行 和 响应首行
            HEADERS, // 打印请求和响应的所有 Header
            BODY     // 所有数据全部打印
        }

        fun setPrintLevel(level: Level): LogInterceptor {
            if (printLevel == null) throw NullPointerException("printLevel == null. Use Level.NONE instead.")
            printLevel = level
            return this
        }

        fun setColorLevel(level: java.util.logging.Level?): LogInterceptor {
            colorLevel = level
            return this
        }

        private fun log(message: String) {
            logger.log(colorLevel!!, message)
        }

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (printLevel == Level.NONE) {
                return chain.proceed(request)
            }
            // 请求日志拦截
            logForRequest(request, chain.connection())
            // 执行请求，计算请求时间
            val startNs = System.nanoTime()
            val response: Response
            response = try {
                chain.proceed(request)
            } catch (e: Exception) {
                log("<-- HTTP FAILED: $e")
                throw e
            }
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            // 响应日志拦截
            return logForResponse(response, tookMs)
        }

        @Throws(IOException::class)
        private fun logForRequest(request: Request, connection: Connection?) {
            val logBody = printLevel == Level.BODY
            val logHeaders = printLevel == Level.BODY || printLevel == Level.HEADERS
            val requestBody = request.body()
            val hasRequestBody = requestBody != null
            val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
            try {
                val requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol
                log(requestStartMessage)
                if (logHeaders) {
                    if (hasRequestBody) {
                        // Request body headers are only present when installed as a network interceptor. Force
                        // them to be included (when available) so there values are known.
                        if (requestBody!!.contentType() != null) {
                            log("\tContent-Type: " + requestBody.contentType())
                        }
                        if (requestBody.contentLength() != -1L) {
                            log("\tContent-Length: " + requestBody.contentLength())
                        }
                    }
                    val headers = request.headers()
                    var i = 0
                    val count = headers.size()
                    while (i < count) {
                        val name = headers.name(i)
                        // Skip headers from the request body as they are explicitly logged above.
                        if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                            log("\t" + name + ": " + headers.value(i))
                        }
                        i++
                    }
                    log(" ")
                    if (logBody && hasRequestBody) {
                        if (isPlaintext(requestBody!!.contentType())) {
                            bodyToString(request)
                        } else {
                            log("\tbody: maybe [binary body], omitted!")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                log("--> END " + request.method())
            }
        }

        private fun logForResponse(response: Response, tookMs: Long): Response {
            val builder = response.newBuilder()
            val clone = builder.build()
            var responseBody = clone.body()
            val logBody = printLevel == Level.BODY
            val logHeaders = printLevel == Level.BODY || printLevel == Level.HEADERS
            try {
                log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）")
                if (logHeaders) {
                    val headers = clone.headers()
                    var i = 0
                    val count = headers.size()
                    while (i < count) {
                        log("\t" + headers.name(i) + ": " + headers.value(i))
                        i++
                    }
                    log(" ")
                    if (logBody && HttpHeaders.hasBody(clone)) {
                        if (responseBody == null) return response
                        if (isPlaintext(responseBody.contentType())) {
                            val bytes = IOUtil.toByteArray(responseBody.byteStream())
                            val contentType = responseBody.contentType()
                            val body = String(bytes, getCharset(contentType)!!)
                            log("\tbody:$body")
                            responseBody = ResponseBody.create(responseBody.contentType(), bytes)
                            return response.newBuilder().body(responseBody).build()
                        } else {
                            log("\tbody: maybe [binary body], omitted!")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                log("<-- END HTTP")
            }
            return response
        }

        private fun bodyToString(request: Request) {
            try {
                val copy = request.newBuilder().build()
                val body = copy.body() ?: return
                val buffer = Buffer()
                body.writeTo(buffer)
                val charset = getCharset(body.contentType())
                log("\tbody:" + buffer.readString(charset!!))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        companion object {
            private val UTF8 = Charset.forName("UTF-8")
            private fun getCharset(contentType: MediaType?): Charset? {
                var charset = if (contentType != null) contentType.charset(UTF8) else UTF8
                if (charset == null) charset = UTF8
                return charset
            }

            private fun isPlaintext(mediaType: MediaType?): Boolean {
                if (mediaType == null) return false
                if (mediaType.type() == "text") {
                    return true
                }
                var subtype = mediaType.subtype()
                subtype = subtype.toLowerCase(Locale.ROOT)
                if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                    return true
                return false
            }
        }
    }

}