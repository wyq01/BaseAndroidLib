package com.wyq.base.office

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.widget.RelativeLayout
import com.blankj.utilcode.util.ToastUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.wyq.base.BuildConfig
import com.wyq.base.R
import com.wyq.base.util.LogUtil
import com.wyq.base.util.ToastUtil
import com.wyq.base.view.BaseDialog
import com.tencent.smtt.sdk.TbsReaderView
import com.wyq.base.BaseActivity
import kotlinx.android.synthetic.main.base_act_tbs_office_reader.*
import permissions.dispatcher.*
import java.io.File

/**
 * 腾讯tbs加载本地文档
 */
@RuntimePermissions
class TbsOfficeReaderAct: BaseActivity(), TbsReaderView.ReaderCallback {

    override fun onCallBackAction(p0: Int?, p1: Any?, p2: Any?) {

    }

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

        title = intent.getStringExtra("title")
        url = intent.getStringExtra("url")
    }

    override fun initViews() {
        super.initViews()

        titleTv?.text = title

        loadWithPermissionCheck()
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
        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
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

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun load() {
        url?.let {
            if (it.startsWith("http")) {
                OkGo.get<File>(it)
                    .tag(this)
                    .execute(object : FileCallback() {
                        override fun downloadProgress(progress: Progress) {
                            LogUtil.d("downloadProgress " + progress.currentSize + "," + progress.totalSize)
                        }
                        override fun onSuccess(response: Response<File>) {
                            LogUtil.d("onSuccess")
                            val file = response.body()
//                            ToastUtil.shortToast(this@TbsOfficeReaderAct, "文档下载完成，路径：${file.path}")
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

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForLoad(request: PermissionRequest) {
        BaseDialog.Builder(this)
            .setPositiveButton(R.string.button_allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.button_deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(R.string.base_permission_rationale)
            .show()
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showDeniedForLoad() {
        ToastUtil.shortToast(this, R.string.base_permission_denied)
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showNeverAskForLoad() {
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
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}