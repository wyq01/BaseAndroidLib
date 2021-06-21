package com.ts.base

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.text.TextUtils
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.HttpParams
import com.ts.base.printer.BluetoothPairService
import com.ts.base.printer.event.BluetoothBindEvent
import com.ts.base.printer.event.BluetoothStatusEvent
import com.ts.base.printer.jqPrinter.JQPrinter
import com.ts.base.printer.jqPrinter.Printer_define
import com.tencent.smtt.sdk.QbSdk
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

/**
 *
 * Created by ts
 * Date: 2019/1/15
 */
open class BaseApplication : MultiDexApplication() {
    companion object {
        const val TAG = "tech_service"
    }

    private var deviceAddress: String? = null
    private var jQPrinter: JQPrinter? = null

    fun getDeviceAddress(): String? {
        return deviceAddress
    }

    fun clearDeviceAddress() {
        setDeviceAddress(null)
    }

    fun setDeviceAddress(deviceAddress: String?) {
        this.deviceAddress = deviceAddress ?: ""
    }

    fun getJQPrinter(): JQPrinter? {
        return jQPrinter
    }

    fun setJQPrinter(printer: JQPrinter?) {
        this.jQPrinter = printer
    }

    override fun onCreate() {
        super.onCreate()

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        QbSdk.initX5Environment(this, null) // 增加这句话

        jQPrinter = JQPrinter(Printer_define.PRINTER_MODEL.JLP351)
        startService(Intent(this, BluetoothPairService::class.java))

        Utils.init(this)
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG).globalTag = TAG

        initOkGo()
    }

    override fun onTerminate() {
        super.onTerminate()

        deviceAddress = null

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        stopService(Intent(this, BluetoothPairService::class.java))
    }

    open fun initOkGo() {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        val headers = HttpHeaders()
        // headers.put("header", "header"); // header不支持中文，不允许有特殊字符
        val params = HttpParams()
        // params.put("commonParamsKey", "这里支持中文参数"); // param支持中文,直接传,不要自己编码

        //----------------------------------------------------------------------------------------//
        val builder = OkHttpClient.Builder()
        // log相关
        val loggingInterceptor = HttpLoggingInterceptor(TAG)
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY) // log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO) // log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor) // 添加OkGo默认debug日志
//        val logInterceptor = OkGoLogInterceptor()
//        logInterceptor.setPrintLevel(OkGoLogInterceptor.Level.BODY) // log打印级别，决定了log显示的详细程度
//        builder.addInterceptor(logInterceptor) // 添加OkGo默认debug日志

        // 第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        // builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        val timeout = 45 * 1000L
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS) // 全局的读取超时时间
        builder.writeTimeout(timeout, TimeUnit.MILLISECONDS) // 全局的写入超时时间
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS) // 全局的连接超时时间

        // 自动管理cookie（或者叫session的保持），以下几种任选其一就行
        // builder.cookieJar(new CookieJarImpl(new SPCookieStore(this))); // 使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(CookieJarImpl(DBCookieStore(this))) // 使用数据库保持cookie，如果cookie不过期，则一直有效
        // builder.cookieJar(new CookieJarImpl(new MemoryCookieStore())); // 使用内存保持cookie，app退出后，cookie消失

        // https相关设置，以下几种方案根据需要自己设置
        // 方法一：信任所有证书,不安全有风险
        val sslParams1 = HttpsUtils.getSslSocketFactory()
        // 方法二：自定义信任规则，校验服务端证书
        val sslParams2 = HttpsUtils.getSslSocketFactory(SafeTrustManager())
        // 方法三：使用预埋证书，校验服务端证书（自签名证书）
        // HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        // 方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        // HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager)
        // 配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        builder.hostnameVerifier(SafeHostnameVerifier())
        builder.addInterceptor { chain ->
            val newBuilder = chain.request().newBuilder()
            chain.proceed(newBuilder.build())
        }

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this) // 必须调用初始化
            .setOkHttpClient(builder.build()) // 建议设置OkHttpClient，不设置会使用默认的
            .setCacheMode(CacheMode.NO_CACHE) // 全局统一缓存模式，默认不使用缓存，可以不传
            .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE) // 全局统一缓存时间，默认永不过期，可以不传
            .setRetryCount(0) // 全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
            .addCommonHeaders(headers) // 全局公共头
            .addCommonParams(params) // 全局公共参数
    }

    /**
     * 这里只是随便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     */
    inner class SafeTrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                for (certificate in chain) {
                    certificate.checkValidity() // 检查证书是否过期，签名是否通过等
                }
            } catch (e: Exception) {
                throw CertificateException(e)
            }

        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    /**
     * 这里只是我随便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     */
    inner class SafeHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            // 验证主机名是否匹配
            // return hostname.equals("server.jeasonlzy.com");
            return true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBluetoothStatusEvent(event: BluetoothStatusEvent) {
        when (event.status) {
            BluetoothAdapter.STATE_OFF // 蓝牙关闭
                , BluetoothAdapter.STATE_DISCONNECTED // 蓝牙断开连接
            -> {
                LogUtils.d("打印机已断开")
                jQPrinter?.close()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBluetoothBindEvent(event: BluetoothBindEvent) {
        when (event.status) {
            BluetoothDevice.BOND_NONE // 解绑设备
            -> if (!TextUtils.isEmpty(event.address) && event.address == deviceAddress) {
                LogUtils.d("打印机已解绑")
                jQPrinter?.close()
                deviceAddress = null
            }
        }
    }
}