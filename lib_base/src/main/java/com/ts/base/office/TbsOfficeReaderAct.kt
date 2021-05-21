package com.ts.base.office

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.widget.RelativeLayout
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.tencent.smtt.sdk.TbsReaderView
import com.ts.base.BaseActivity
import com.ts.base.BuildConfig
import com.ts.base.R
import com.ts.base.util.ToastUtil
import com.ts.base.view.BaseDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.base_act_tbs_office_reader.*
import java.io.File

/**
 * 腾讯tbs加载本地文档
 */
class TbsOfficeReaderAct: BaseActivity(), TbsReaderView.ReaderCallback {

    companion object {
        fun startActivity(context: Context, title: String, url: String) {
            val intent = Intent(context, TbsOfficeReaderAct::class.java)
            intent.putExtra("title", title)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    private var name: String? = null
    private var url: String? = null

    private var tbsReaderView: TbsReaderView? = null

    override fun initData(intent: Intent) {
        super.initData(intent)

        name = intent.getStringExtra("title")
        url = intent.getStringExtra("url")
    }

    override fun initViews() {
        super.initViews()

        titleTv?.text = name

        load()
    }

    private fun load() {
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
                    download()
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

    private fun download() {
        url?.let {
            if (it.startsWith("http")) {
                OkGo.get<File>(it)
                    .tag(this)
                    .execute(object : FileCallback() {
                        override fun downloadProgress(progress: Progress) {
                            LogUtils.d("downloadProgress " + progress.currentSize + "," + progress.totalSize)
                        }
                        override fun onSuccess(response: Response<File>) {
                            LogUtils.d("onSuccess")
                            val file = response.body()
                            displayFile(file.path)
                        }
                    })
            } else {
                val file = File(it)
                if (!file.exists()) {
                    ToastUtils.showShort("文件不存在")
                    return
                }
                displayFile(it)
            }
        }
    }

    private val tbsReaderTemp = Environment.getExternalStorageDirectory().toString() + "/TbsReaderTemp"
    private fun displayFile(filePath: String) {
        tbsReaderView = TbsReaderView(this, this)
        container.addView(
            tbsReaderView,
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        )
        // 增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        val bsReaderTempFile = File(tbsReaderTemp)
        if (!bsReaderTempFile.exists()) {
            val mkdir = bsReaderTempFile.mkdir()
            if (!mkdir) {
                ToastUtils.showShort("加载文档异常")
                finish()
                return
            }
        }

        val result = tbsReaderView?.preOpen(getFileType(filePath), false)
        result?.let {
            if (it) {
                val bundle = Bundle()
                bundle.putString("filePath", filePath)
                bundle.putString("tempPath", tbsReaderTemp)
                tbsReaderView?.openFile(bundle)
            }
        }
    }

    private fun getFileType(paramString: String): String {
        var str = ""
        if (TextUtils.isEmpty(paramString)) {
            return str
        }
        val i = paramString.lastIndexOf('.')
        if (i <= -1) {
            return str
        }
        str = paramString.substring(i + 1)
        return str
    }

    override fun initLayout(): Int {
        return R.layout.base_act_tbs_office_reader
    }

    override fun overStatusBar(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        tbsReaderView?.onStop()
    }

    override fun onCallBackAction(p0: Int?, p1: Any?, p2: Any?) {}

}