package com.wyq.base.qrcode

import android.app.Activity
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.PermissionUtils
import com.gyf.barlibrary.ImmersionBar
import com.king.zxing.CaptureActivity
import com.king.zxing.Intents
import com.king.zxing.util.CodeUtils
import com.wyq.base.BuildConfig
import com.wyq.base.R
import com.wyq.base.constant.RequestCode
import com.wyq.base.util.ToastUtil
import com.wyq.base.util.click
import com.wyq.base.view.BaseDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.base_act_qrcode.*

/**
 * Created by wyq
 * Date: 2019/1/14
 */
class QRCodeAct : CaptureActivity() {

    companion object {
        @JvmStatic
        fun startActivityForResult(activity: Activity) {
            startActivityForResult(activity, RequestCode.REQUEST_SCAN)
        }
        @JvmStatic
        fun startActivityForResult(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, QRCodeAct::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.base_act_qrcode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mImmersionBar = ImmersionBar.with(this)
        val paddingView = findViewById<View>(R.id.paddingView)
        if (paddingView != null) {
            mImmersionBar.statusBarView(paddingView)
                    .statusBarColor(android.R.color.transparent)
        }

        mImmersionBar.keyboardEnable(true)
                .statusBarDarkFont(false)
                .navigationBarColor(android.R.color.white)
                .init()
        beepManager.setPlayBeep(true)
        beepManager.setVibrate(true)

        backBtn.click { finish() }

        lightBtn.click { view ->
            if (view.isSelected) {
                offFlash()
                view.isSelected = false
            } else {
                openFlash()
                view.isSelected = true
            }
        }

        rightBtn.click {
            album()
        }
    }

    private fun offFlash() {
        val camera = cameraManager.openCamera.camera
        val parameters = camera.parameters
        parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
        camera.parameters = parameters
    }

    private fun openFlash() {
        val camera = cameraManager.openCamera.camera
        val parameters = camera.parameters
        parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
        camera.parameters = parameters
    }

    private fun album() {
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
                    Matisse.from(this)
                        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                        .theme(R.style.Matisse_Dracula)
                        .countable(false)
                        .capture(true)
                        .captureStrategy(CaptureStrategy(true, BuildConfig.APPLICATION_ID + ".fileprovider"))
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
            }
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.REQUEST_PHOTO -> {
                    data?.let {
                        val list = Matisse.obtainPathResult(it)
                        val photo = list[0]
                        Observable.create(ObservableOnSubscribe<String> { emitter ->
                            val result = CodeUtils.parseCode(photo)
                            if (!TextUtils.isEmpty(result)) {
                                emitter.onNext(result)
                            } else {
                                emitter.onError(Throwable(""))
                            }
                        }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<String> {
                                override fun onSubscribe(d: Disposable) {}
                                override fun onNext(result: String) {
                                    val intent = Intent()
                                    intent.putExtra(Intents.Scan.RESULT, result)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                                override fun onError(e: Throwable) {
                                    ToastUtil.shortToast(this@QRCodeAct, "无法获取条形码或二维码")
                                }
                                override fun onComplete() {}
                            })
                    } ?: let {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            }
        }
    }

}