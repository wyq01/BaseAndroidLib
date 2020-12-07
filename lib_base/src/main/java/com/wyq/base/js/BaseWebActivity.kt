package com.wyq.base.js

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.webkit.*
import cn.pedant.SafeWebViewBridge.InjectedChromeClient
import cn.pedant.SafeWebViewBridge.JsCallback
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.wyq.base.BaseActivity
import com.wyq.base.BuildConfig
import com.wyq.base.R
import com.wyq.base.constant.RequestCode
import com.wyq.base.printer.event.PrintResultEvent
import com.wyq.base.sign.SignActivity
import com.wyq.base.sign.config.PenConfig
import com.wyq.base.util.ScreenRotateUtils
import com.wyq.base.util.ToastUtil
import com.wyq.base.util.applyJson
import com.wyq.base.view.BaseDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.base_act_web.*
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * 基础js交互页
 */
abstract class BaseWebActivity : BaseActivity() {

    protected abstract fun url(): String
    protected var mWebView: WebView? = null

    companion object {
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

        this.mWebView = webView
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
                LogUtils.d("shouldOverrideUrlLoading ${request.url}")
                // 解决重定向导致无法goback
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                LogUtils.d("shouldOverrideUrlLoading $url")
                // 解决重定向导致无法goback
                return false
            }

            override fun onPageFinished(view: WebView?, url: String) {
                super.onPageFinished(view, url)
                if (TextUtils.isEmpty(homeUrl) && encodeUrl(url()) != url) {
                    homeUrl = url
                }
                LogUtils.d("onPageFinished:$url")
            }
        }
        LogUtils.d("url:${url()}")
        webView.loadUrl(encodeUrl(url()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            RequestCode.REQUEST_FILE -> {
                if (null == mUploadMessage && null == mUploadCallbackAboveL) return
                val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
                if (mUploadCallbackAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data)
                } else if (mUploadMessage != null) {
                    mUploadMessage?.onReceiveValue(result)
                    mUploadMessage = null
                }
            }
            RequestCode.REQUEST_SIGN -> {
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
            RequestCode.REQUEST_PHOTO -> {
                data?.let {
                    val list = Matisse.obtainPathResult(it)
                    val photo = list[0]
                    jsCallback?.let { j ->
                        try {
                            j.apply(photo)
                        } catch (e: JsCallback.JsCallbackException) {
                            e.printStackTrace()
                        }
                    }
                } ?: let {
                    jsCallback?.let { j ->
                        try {
                            j.apply("")
                        } catch (e: JsCallback.JsCallbackException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != RequestCode.REQUEST_FILE || mUploadCallbackAboveL == null) {
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
            this@BaseWebActivity.startActivityForResult(Intent.createChooser(i, "File Chooser"), RequestCode.REQUEST_FILE)
        }

        // For Android 3.0+
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            this@BaseWebActivity.startActivityForResult(Intent.createChooser(i, "File Browser"), RequestCode.REQUEST_FILE)
        }

        //For Android 4.1
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            this@BaseWebActivity.startActivityForResult(Intent.createChooser(i, "File Browser"), RequestCode.REQUEST_FILE)
        }

        // For Android 5.0+
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            mUploadCallbackAboveL = filePathCallback
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            this@BaseWebActivity.startActivityForResult(Intent.createChooser(i, "File Browser"), RequestCode.REQUEST_FILE)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPrintResultEvent(event: PrintResultEvent) {
        try {
            jsCallback?.apply(event.success)
            jsCallback = null
        } catch (e: JsCallback.JsCallbackException) {
            e.printStackTrace()
        }
    }

    /********************** 以下为交互方法 *********************/
    private var jsCallback: JsCallback? = null

    /**
     * 打印
     */
    fun print(json: String?, jsCallback: JsCallback) {
        this.jsCallback = jsCallback
        print(json)
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

    fun album(needCapture: Boolean = true, jsCallback: JsCallback) {
        this.jsCallback = jsCallback
        AndPermission.with(this)
            .runtime()
            .permission(Permission.Group.STORAGE, Permission.Group.CAMERA)
            .onGranted {
                var selfPermission = true
                for (item in it) {
                    if (!PermissionUtils.isGranted(item)) {
                        selfPermission = false
                        break
                    }
                }
                if (selfPermission) {
                    Matisse.from(this)
                        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                        .theme(R.style.Matisse_Dracula)
                        .countable(false)
                        .capture(needCapture)
                        .captureStrategy(CaptureStrategy(true, this.packageName + ".fileprovider"))
                        .maxSelectable(1)
                        .imageEngine(GlideEngine())
                        .forResult(RequestCode.REQUEST_PHOTO)
                } else {
                    ToastUtil.shortToast(this, R.string.base_permission_denied)
                }
            }
            .rationale { _, _, executor ->
                BaseDialog.Builder(this)
                    .setPositiveButton(R.string.button_allow) { _, _ -> executor.execute() }
                    .setNegativeButton(R.string.button_deny) { _, _ -> executor.cancel() }
                    .setCancelable(false)
                    .setMessage(R.string.base_permission_rationale)
                    .show()
            }
            .onDenied {
                if (AndPermission.hasAlwaysDeniedPermission(this, it)) {
                    BaseDialog.Builder(this)
                        .setPositiveButton(R.string.button_ok) { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.button_cancel, null)
                        .setCancelable(false)
                        .setMessage(R.string.base_permission_setting)
                        .show()
                } else {
                    ToastUtil.shortToast(this, R.string.base_permission_denied)
                }
            }.start()
    }

    fun singleUpload(path: String, url: String, timeout: Long, jsCallback: JsCallback) {
        if (TextUtils.isEmpty(path)) {
            uploadResult(-1, "图片路径不能为空", null, jsCallback)
            return
        }
        val file = File(path)
        if (!file.exists()) {
            uploadResult(-1, "图片不存在", null, jsCallback)
            return
        }
        jsCallback.setPermanent(true)
        OkGo.post<String>(url)
            .client(OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.MILLISECONDS).build())
            .tag(this@BaseWebActivity)
            .params("file", file)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response?.let {
                        uploadResult(1, "", it.body(), jsCallback)
                    }
                }
                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    response?.let {
                        uploadResult(-1, it.exception.message, null, jsCallback)
                    }
                }
                override fun uploadProgress(progress: Progress?) {
                    super.uploadProgress(progress)
                    progress?.let {
                        uploadResult(0, "", Gson().toJson(progress), jsCallback)
                    }
                }
            })
    }

    fun cancelUpload() {
        OkGo.getInstance().cancelTag(this@BaseWebActivity)
    }

    private fun uploadResult(code: Int, message: String?, data: String?, jsCallback: JsCallback) {
        val json = JSONObject()
        json.put("code", code)
        message?.let {
            json.put("message", it)
        } ?: let {
            json.put("message", "")
        }
        data?.let {
            try {
                json.put("data", JSONObject(it))
            } catch (e: JSONException) {
                json.put("data", data)
            }
        } ?: let {
            json.put("data", JSONObject())
        }
        Log.i("techservice", json.toString())
        try {
            jsCallback.applyJson(json.toString())
        } catch (e: JsCallback.JsCallbackException) {
            e.printStackTrace()
        }
    }

}