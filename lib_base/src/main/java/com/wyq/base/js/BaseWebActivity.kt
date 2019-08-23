package com.wyq.base.js

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.webkit.*
import cn.pedant.SafeWebViewBridge.InjectedChromeClient
import cn.pedant.SafeWebViewBridge.JsCallback
import com.wyq.base.BaseActivity
import com.wyq.base.R
import com.wyq.base.printer.bean.PrintResult
import com.wyq.base.sign.SignActivity
import com.wyq.base.sign.config.PenConfig
import com.wyq.base.util.LogUtil
import com.wyq.base.util.ScreenRotateUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.base_act_web.*
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * 基础js交互页
 */
abstract class BaseWebActivity : BaseActivity() {

    protected abstract fun url(): String

    companion object {
        const val RESULT_CODE_FILE_CHOOSE = 1
    }

    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mUploadCallbackAboveL: ValueCallback<Array<Uri>>? = null

    private var homeUrl: String? = null // 用于判断url是否有二次跳转导致无法goback

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        super.initViews()

        mImmersionBar?.statusBarColor(android.R.color.white)
            ?.statusBarDarkFont(true)
            ?.init()

        WebView.setWebContentsDebuggingEnabled(true)
        val ws = webView.settings
        ws.setSupportZoom(false)
        ws.builtInZoomControls = false
        ws.cacheMode = WebSettings.LOAD_DEFAULT
        ws.javaScriptCanOpenWindowsAutomatically = true
        ws.allowFileAccessFromFileURLs = true
        ws.allowFileAccess = true // 设置允许访问文件数据
        ws.databaseEnabled = false
        ws.domStorageEnabled = true
        ws.javaScriptEnabled = true
        ws.setAppCacheEnabled(false)

        webView.webChromeClient = CustomChromeClient("plus", JsScope::class.java)
        webView.webViewClient = object : WebViewClient() {
            /**
             * 本地jquery.js
             */
            private fun jquery(): WebResourceResponse? {
                try {
                    return WebResourceResponse("application/x-javascript", "utf-8", assets.open("jquery.js"))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                //需处理特殊情况
                return null
            }

            /**
             * 本地react.development.js
             */
            private fun reactDevelopment(): WebResourceResponse? {
                try {
                    return WebResourceResponse("application/x-javascript", "utf-8", assets.open("react.development.js"))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                //需处理特殊情况
                return null
            }

            /**
             * 本地react-dom.development.js
             */
            private fun reactDom(): WebResourceResponse? {
                try {
                    return WebResourceResponse("application/x-javascript", "utf-8", assets.open("react-dom.development.js"))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                //需处理特殊情况
                return null
            }

            /**
             * 本地babel.min.js
             */
            private fun babelMin(): WebResourceResponse? {
                try {
                    return WebResourceResponse("application/x-javascript", "utf-8", assets.open("babel.min.js"))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                //需处理特殊情况
                return null
            }

            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                if (Build.VERSION.SDK_INT < 21) {
                    when {
                        url.contains("jquery") -> return jquery()
                        url.contains("react.development") -> reactDevelopment()
                        url.contains("react-dom.development") -> reactDom()
                        url.contains("babel.min") -> babelMin()
                    }
                }
                return super.shouldInterceptRequest(view, url)
            }

            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                if (Build.VERSION.SDK_INT >= 21) {
                    val url = request.url.toString()
                    if (!TextUtils.isEmpty(url)) {
                        when {
                            url.contains("jquery") -> return jquery()
                            url.contains("react.development") -> reactDevelopment()
                            url.contains("react-dom.development") -> reactDom()
                            url.contains("babel.min") -> babelMin()
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                LogUtil.d("shouldOverrideUrlLoading ${request.url}")
                // 解决重定向导致无法goback
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                LogUtil.d("shouldOverrideUrlLoading $url")
                // 解决重定向导致无法goback
                return false
            }

            override fun onPageFinished(view: WebView?, url: String) {
                super.onPageFinished(view, url)
                if (TextUtils.isEmpty(homeUrl) && encodeUrl(url()) != url) {
                    homeUrl = url
                }
                LogUtil.d("onPageFinished:$url")
            }
        }
        LogUtil.d("url:${url()}")
        webView.loadUrl(encodeUrl(url()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            RESULT_CODE_FILE_CHOOSE -> {
                if (null == mUploadMessage && null == mUploadCallbackAboveL) return
                val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
                if (mUploadCallbackAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data)
                } else if (mUploadMessage != null) {
                    mUploadMessage?.onReceiveValue(result)
                    mUploadMessage = null
                }
            }
            SignActivity.REQUEST_SIGN -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let { d ->
                        val path = d.getStringExtra(PenConfig.SAVE_PATH)
                        jsCallback?.let { j ->
                            try {
                                j.apply(path)
                            } catch (e: JsCallback.JsCallbackException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != RESULT_CODE_FILE_CHOOSE || mUploadCallbackAboveL == null) {
            return
        }
        val results = ArrayList<Uri>()
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                val dataString = it.dataString
                val clipData = it.clipData

                clipData?.let { c ->
                    results.clear()
                    for (i in 0 until c.itemCount) {
                        val item = clipData.getItemAt(i)
                        results.add(item.uri)
                    }
                }

                dataString?.let { d ->
                    results.clear()
                    results.add(Uri.parse(d))
                }
            }
        }
        mUploadCallbackAboveL?.onReceiveValue(results.toTypedArray())
        mUploadCallbackAboveL = null
        return
    }

    override fun onBackPressed() {
        val webBackForwardList = webView.copyBackForwardList()
        val currentUrl = webBackForwardList.currentItem?.url
        if (webView.canGoBack() && !currentUrl.equals(homeUrl)) { // 不处于重定向页面时，回退到上一层
            webView.goBack()
        } else { // 退出WebView
            super.onBackPressed()
        }
    }

    inner class CustomChromeClient internal constructor(injectedName: String, injectedCls: Class<*>) : InjectedChromeClient(injectedName, injectedCls) {
        // For Android 3.0-
        fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            this@BaseWebActivity.startActivityForResult(
                Intent.createChooser(i, "File Chooser"),
                RESULT_CODE_FILE_CHOOSE
            )
        }

        // For Android 3.0+
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            this@BaseWebActivity.startActivityForResult(
                Intent.createChooser(i, "File Browser"),
                RESULT_CODE_FILE_CHOOSE
            )
        }

        //For Android 4.1
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            this@BaseWebActivity.startActivityForResult(
                Intent.createChooser(i, "File Browser"),
                RESULT_CODE_FILE_CHOOSE
            )
        }

        // For Android 5.0+
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            mUploadCallbackAboveL = filePathCallback
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            this@BaseWebActivity.startActivityForResult(
                Intent.createChooser(i, "File Browser"),
                RESULT_CODE_FILE_CHOOSE
            )
            return true
        }
    }

    private fun encodeUrl(url: String): String {
        val charArr = url.toCharArray()
        val result = StringBuffer()
        for (char in charArr) {
            if (isChinese(char)) {
                try {
                    val encode = URLEncoder.encode(char.toString(), "UTF-8")
                    result.append(encode)
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            } else {
                result.append(char)
            }
        }
        return result.toString()
    }

    private fun isChinese(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION)
    }

    override fun initLayout(): Int {
        return R.layout.base_act_web
    }

    override fun overStatusBar(): Boolean {
        return false
    }

    /********************** 以下为交互方法 *********************/
    private var jsCallback: JsCallback? = null
    /**
     * 打印
     */
    @Deprecated("旧版打印，已弃用")
    fun print(header: String?, title: String?, content: String?, stamp: String?, twice: Boolean, jsCallback: JsCallback) {
        Observable.create(ObservableOnSubscribe<Boolean> { emitter ->
            var printResult = false
            try {
                print(header, title, content, stamp, twice)
                printResult = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                emitter.onNext(printResult)
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(result: Boolean) {
                    try {
                        jsCallback.apply(result)
                    } catch (e: JsCallback.JsCallbackException) {
                        e.printStackTrace()
                    }
                }
                override fun onError(e: Throwable) {
                    try {
                        jsCallback.apply(false)
                    } catch (e: JsCallback.JsCallbackException) {
                        e.printStackTrace()
                    }
                }
                override fun onComplete() {}
            })
    }

    /**
     * 打印
     */
    fun print(json: String?, jsCallback: JsCallback) {
        Observable.create(ObservableOnSubscribe<Int> { emitter ->
            try {
                emitter.onNext(print(json))
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onNext(PrintResult.NONE)
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(result: Int) {
                    try {
                        when(result) {
                            PrintResult.FAILED -> jsCallback.apply(false)
                            PrintResult.SUCCESS -> jsCallback.apply(true)
                            else -> {}
                        }
                    } catch (e: JsCallback.JsCallbackException) {
                        e.printStackTrace()
                    }
                }
                override fun onError(e: Throwable) {
                    try {
                        jsCallback.apply(false)
                    } catch (e: JsCallback.JsCallbackException) {
                        e.printStackTrace()
                    }
                }
                override fun onComplete() {}
            })
    }

    /**
     * 签名
     */
    fun sign(jsCallback: JsCallback) {
        this.jsCallback = jsCallback
        SignActivity.startActivityForResult(this)
    }

    /**
     * 切换屏幕方向
     */
    fun toggleScreenOrientation() {
        changeScreenOrientation()
    }

    /**
     * 屏幕是否为横屏
     */
    fun screenIsLandscape(jsCallback: JsCallback) {
        try {
            jsCallback.apply(screenIsLandscape())
        } catch (e: JsCallback.JsCallbackException) {
            e.printStackTrace()
        }
    }

    /**
     * 注册重力感应监听
     */
    override fun onResume() {
        super.onResume()
        ScreenRotateUtils.getInstance(this).registerSensorRotate(this)
    }

    /**
     * 解除重力感应监听
     */
    override fun onPause() {
        super.onPause()
        ScreenRotateUtils.getInstance(this).unregisterSensorRotate()
    }

    /**
     * 打开或关闭重力感应
     */
    fun enableSensorRotate(sensorRotate: Boolean) {
//        requestedOrientation = if (sensorRotate) {
//            ActivityInfo.SCREEN_ORIENTATION_SENSOR
//        } else {
//            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        }
        ScreenRotateUtils.getInstance(this).enableSensorRotate(sensorRotate)
    }

}