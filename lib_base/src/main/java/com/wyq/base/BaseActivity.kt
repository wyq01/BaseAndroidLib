package com.wyq.base

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.widget.AppCompatImageButton
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.gyf.barlibrary.ImmersionBar
import com.smarx.notchlib.NotchScreenManager
import com.wyq.base.constant.RequestCode
import com.wyq.base.event.BaseEvent
import com.wyq.base.printer.PrinterConnectAct
import com.wyq.base.printer.bean.BasePrint
import com.wyq.base.printer.bean.PrintBean
import com.wyq.base.printer.event.PrintResultEvent
import com.wyq.base.printer.jqPrinter.JQPrinter
import com.wyq.base.printer.jqPrinter.esc.ESC
import com.wyq.base.sign.util.BitmapUtil
import com.wyq.base.util.ScreenRotateUtils
import com.wyq.base.util.ToastUtil
import com.wyq.base.util.click
import com.wyq.base.view.BaseDialog
import com.wyq.base.view.LoadingLayout
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

/**
 * 基础activity
 */
abstract class BaseActivity : BaseAbstractActivity() {

    companion object {
        const val SIGNATURE_WIDTH = 150
        private const val FINISH_DELAY_TIME: Long = 300
    }

    protected var mImmersionBar: ImmersionBar? = null
    private var paddingView: View? = null
    protected var titleTv: TextView? = null
    protected var rightBtn: AppCompatImageButton? = null
    protected var rightTv: TextView? = null
    private var loadingLayout: LoadingLayout? = null

    private var layoutContainer: FrameLayout? = null

    private var uiLoadIsFinished = false // UI加载是否完成

    private var notchHeight = -1 // 刘海高度
    private var layoutContentView: View? = null // 布局内容
    private var fixNotch = true // 是否打开刘海适配，默认打开
    private var containerId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notchScreenManager = NotchScreenManager.getInstance()
        notchScreenManager.setDisplayInNotch(this)
        notchScreenManager.getNotchInfo(this) { notchScreenInfo ->
            if (notchScreenInfo.hasNotch) {
                for (rect in notchScreenInfo.notchRects) {
                    if (ScreenRotateUtils.screenIsLandscape(this)) {
                        notchHeight = rect.right
                        initNotchScreen()
                    } else {
                        notchHeight = rect.bottom
                    }
                }
            }
        }

        val contentView = customContentView(View.inflate(this, if (overStatusBar()) R.layout.base_act_base_without_padding else R.layout.base_act_base, null))
        setContentView(contentView)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        paddingView = findViewById(R.id.paddingView)
        mImmersionBar = ImmersionBar.with(this)
        if (!overStatusBar()) {
            paddingView?.let {
                mImmersionBar?.statusBarView(it)
                    ?.statusBarColor(R.color.base_theme)
            }
        } else {
            paddingView?.let {
                mImmersionBar?.statusBarView(it)
                    ?.statusBarColor(android.R.color.transparent)
            }
        }
        mImmersionBar?.keyboardEnable(true)
            ?.statusBarDarkFont(false)
            ?.navigationBarColor(android.R.color.white)
            ?.init()
        initData(intent)
        initViews()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !uiLoadIsFinished) {
            uiLoadIsFinished = true
            lazyInitViews()
            // 页面加载完毕后开始获取数据
            getData()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        initNotchScreen()
    }

    fun fixNotch(fixNotch: Boolean) {
        fixNotch(fixNotch, -1)
    }

    fun fixNotch(fixNotch: Boolean, containerId: Int) {
        this.fixNotch = fixNotch
        this.containerId = containerId
    }

    /**
     * 修改横屏布局
     */
    private fun initNotchScreen() {
        if (fixNotch) {
            var left = 0
            var right = 0
            when(requestedOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    left = notchHeight
                    right = 0
                }
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                    left = 0
                    right = notchHeight
                }
            }
            if (containerId != -1) {
                val containerView = findViewById<View>(containerId)
                if (containerView != null) {
                    containerView.setPadding(left, 0, right, 0)
                } else {
                    layoutContentView?.setPadding(left, 0, right, 0)
                }
            } else {
                layoutContentView?.setPadding(left, 0, right, 0)
            }
        }
    }

    /**
     * 显示空页面
     */
    protected fun showEmpty() {
        loadingLayout?.showEmpty()
    }

    /**
     * 显示加载框
     */
    protected fun showLoading() {
        loadingLayout?.showLoading()
    }

    /**
     * 显示无网络页面
     */
    protected fun showNoNetwork() {
        loadingLayout?.showNoNetwork()
    }

    /**
     * 显示内容
     */
    protected fun showContent() {
        loadingLayout?.showContent()
    }

    /**
     * 显示空
     */
    protected fun showNone() {
        loadingLayout?.showNone()
    }

    /**
     * 重新加载数据
     */
    protected open fun retryLoad() {}

    /**
     * 是否覆盖状态栏
     */
    protected abstract fun overStatusBar(): Boolean

    private fun customContentView(rootView: View): View {
        layoutContentView = View.inflate(this, initLayout(), null)
        layoutContentView?.let { content ->
            loadingLayout = content.findViewById(R.id.loadingLayout)
            loadingLayout?.let { loading ->
                showLoading()
                loading.setOnRetryListener {
                    showLoading()
                    retryLoad()
                }
            }
            layoutContainer = rootView.findViewById<FrameLayout>(R.id.layoutContainer)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            layoutContainer?.addView(content, params)

            val backBtn = content.findViewById<ImageButton>(R.id.backBtn)
            backBtn?.click {
                finish()
                KeyboardUtils.hideSoftInput(this@BaseActivity)
            }
            titleTv = content.findViewById(R.id.titleTv)
            rightBtn = content.findViewById(R.id.rightBtn)
            rightTv = content.findViewById(R.id.rightTv)
        }
        return rootView
    }

    /** 设置根布局透明 */
    protected fun setBackgroundTransparent() {
        layoutContainer?.setBackgroundColor(resources.getColor(android.R.color.transparent))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBaseEvent(event: BaseEvent) {
        finishDelayed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mImmersionBar?.destroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        KeyboardUtils.hideSoftInput(this)
    }

    @JvmOverloads
    protected fun finishDelayed(time: Long = FINISH_DELAY_TIME) {
        Handler().postDelayed({ finish() }, time)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            RequestCode.REQUEST_PRINTER_CONNECT -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        if (PrinterConnectAct.continuePrint(data)) {
                            Handler().postDelayed({
                                print(printContent)
                            }, 300)
                        }
                    }
                } else {
                    if (PrinterConnectAct.continuePrint(data)) {
                        EventBus.getDefault().post(PrintResultEvent(false, printContent, this.localClassName))
                    }
                }
            }
        }
    }

    private var printContent: String? = null
    /**
     * 打印
     */
    protected fun print(json: String?) {
        printContent = json
        AndPermission.with(this)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .onGranted {
                var selfPermission = true
                for (item in it) {
                    if (!PermissionUtils.isGranted(item)) {
                        selfPermission = false
                        break
                    }
                }
                if (selfPermission) {
                    printImpl(json)
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
            }
            .start()
    }

    /**
     * 开始打印
     */
    private fun printImpl(json: String?) {
        val printer = (application as BaseApplication).getJQPrinter()
        if (printer != null && printer.canPrint()) {
            try {
                val list = Gson().fromJson(json, Array<PrintBean>::class.java)
                LogUtils.d("开始打印")
                for (item in list) {
                    when (item.type) {
                        BasePrint.Type.TEXT.value -> printText(item, printer)
                        BasePrint.Type.LINE.value -> printLine(item, printer)
                        BasePrint.Type.BLANK.value -> printBlank(item, printer)
                        BasePrint.Type.ENTER.value -> printEnter(printer)
                        BasePrint.Type.STAMP.value -> printStamp(item, printer)
                        BasePrint.Type.SIGNATURE.value -> printSignature(item, printer)
                        BasePrint.Type.QRCODE.value -> printQRCode(item, printer)
                    }
                }
                EventBus.getDefault().post(PrintResultEvent(true, printContent, this.localClassName))
            } catch (e1: IOException) {
                e1.printStackTrace()
                ToastUtil.shortToast(this, e1.message!!)
                EventBus.getDefault().post(PrintResultEvent(false, printContent, this.localClassName))
            } catch (e2: JsonSyntaxException) {
                e2.printStackTrace()
                ToastUtil.shortToast(this, "json格式不正确")
                EventBus.getDefault().post(PrintResultEvent(false, printContent, this.localClassName))
            } catch (e3: java.lang.NullPointerException) {
                e3.printStackTrace()
                ToastUtil.shortToast(this, e3.message!!)
                EventBus.getDefault().post(PrintResultEvent(false, printContent, this.localClassName))
            }
        } else {
            (application as BaseApplication).clearDeviceAddress()
            PrinterConnectAct.startActivity(this, true)
        }
    }

    /**
     * 打印文本
     */
    private fun printText(bean: PrintBean, printer: JQPrinter) {
        bean.text?.let {
            printer.esc.text.print(bean.revertAlign(), bean.revertTextSize(), bean.bold, bean.underLine, bean.text)
            if (bean.enter) {
                printEnter(printer)
            }
        } ?: let {
            throw NullPointerException("类型为${bean.type}，text不可为空")
        }
    }

    /**
     * 打印分割线
     */
    private fun printLine(bean: PrintBean, printer: JQPrinter) {
        val lines = arrayOfNulls<ESC.LINE_POINT>(1)
        lines[0] = ESC.LINE_POINT(0, 575)
        bean.lineTip?.let {
            if (!TextUtils.isEmpty(it)) {
                printer.esc.text.print(it)
            }
        } ?: let {
            printer.esc.text.print("请沿此处撕开")
        }
        printEnter(printer)
        printer.esc.text.print("-----------------------------------------------")
        printEnter(printer)
    }

    /**
     * 打印空白
     */
    private fun printBlank(bean: PrintBean, printer: JQPrinter) {
        if (bean.blankHeight < 0) {
            throw NullPointerException("类型为${bean.type}，blankHeight不可为负数")
        } else {
            printer.esc.feedDots(bean.blankHeight)
        }
    }

    /**
     * 打印换行
     */
    private fun printEnter(printer: JQPrinter) {
        printer.esc.feedEnter()
    }

    /**
     * 打印印章
     */
    private fun printStamp(bean: PrintBean, printer: JQPrinter) {
        bean.stamp?.let {
            if (it.isNotEmpty()) {
                val rectStartX = 125
                val rect = BitmapUtil.zoomImg(ImageUtils.getBitmap(R.drawable.base_ic_stamp_rect), 320)
                printer.esc.image.drawBitmap(rectStartX, 0, rect)
                val startX = rectStartX + 16
                printer.esc.text.print(startX + 0, (rect.height * 0.7f).toInt(), ESC.FONT_HEIGHT.x32, "姑苏区“四类行业”")
                printer.esc.text.print(startX + 80, (rect.height * 0.42f).toInt(), ESC.FONT_HEIGHT.x32, "整治专班")
                printer.esc.text.print(startX + 144 - it.length * 12, (rect.height * 0.1f).toInt(), ESC.FONT_HEIGHT.x24, it)
                printEnter(printer)
            }
        } ?: let {
            throw NullPointerException("类型为${bean.type}，stamp不可为空")
        }
    }

    /**
     * 打印签名
     */
    private fun printSignature(bean: PrintBean, printer: JQPrinter) {
        var signBmHeight = 0
        bean.signPath?.let {
            val signBm = BitmapFactory.decodeFile(it)
            if (signBm != null) {
                val bm = BitmapUtil.zoomImg(signBm, bean.signWidth)
                signBmHeight = bm.height / 2
                printer.esc.image.drawBitmap((bean.signTip?.length ?: 0) * bean.getTextSizeHeight() + (if (TextUtils.isEmpty(bean.signTip)) 0 else 10), 0, bm)
            }
        } ?: let {
            throw NullPointerException("类型为${bean.type}，imgPath不可为空")
        }
        if (bean.showSignTip) {
            bean.signTip?.let {
                printer.esc.text.print(0, signBmHeight - bean.getTextSizeHeight() / 4, bean.revertTextSize(), bean.bold, bean.underLine, it)
                printEnter(printer)
            }
        }
    }

    /**
     * 打印二维码
     */
    private fun printQRCode(bean: PrintBean, printer: JQPrinter) {
        bean.text?.let {
            when(bean.align) {
                BasePrint.Align.CENTER.value, BasePrint.Align.RIGHT.value ->
                    printer.esc.barcode.printQRCode(bean.revertAlign(), ESC.BAR_UNIT.x4, 0, 2, it)
                else ->
                    printer.esc.barcode.printQRCode(bean.startX, bean.startY, ESC.BAR_UNIT.x4, 0, 2, it)
            }
            printEnter(printer)
        } ?: let {
            throw NullPointerException("类型为${bean.type}，text不可为空")
        }
    }

    /**
     * 切换屏幕方向
     */
    fun changeScreenOrientation() {
        ScreenRotateUtils.changeScreenOrientation(this)
        ScreenRotateUtils.getInstance(this).enableSensorRotate(false)
    }

    /**
     * 屏幕是否为横屏
     */
    fun screenIsLandscape(): Boolean {
        return ScreenRotateUtils.screenIsLandscape(this)
    }

}