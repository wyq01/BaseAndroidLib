package com.wyq.example.basic

import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.ts.upgrade.UpgradeUtils
import com.wyq.base.BaseActivity
import com.wyq.base.office.TbsOfficeReaderAct
import com.wyq.base.printer.event.PrintResultEvent
import com.wyq.base.util.ToastUtil
import com.wyq.base.util.click
import com.wyq.base.view.BaseDialog
import com.wyq.base.view.VerticalDividerWithoutTB
import com.wyq.example.R
import com.wyq.example.basic.adapter.HistoryAdapter
import com.wyq.example.basic.util.Urls
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.act_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainAct : BaseActivity() {

    private var historyAdapter: HistoryAdapter? = null

    override fun initViews() {
        super.initViews()

        fixNotch(true)

        historyAdapter = HistoryAdapter(this, ArrayList())

        val manager = LinearLayoutManager(this)
        historyRv.layoutManager = manager
        historyRv.addItemDecoration(
            VerticalDividerWithoutTB(
                this,
                1,
                resources.getColor(R.color.base_divider)
            )
        )
        historyRv.adapter = historyAdapter

        enterBtn.click {
            val url = urlEt.text.toString().trim()
            if (url.isEmpty()) {
                ToastUtil.shortToast(this, "请输入正确的地址")
            } else {
                WebViewAct.startActivity(this, url)
                Urls.saveUrl(this, url)
                updateHistory()
            }
        }

        clearBtn.click {
            BaseDialog.Builder(this)
                    .setMessage("确定清除历史记录？")
                    .setPositiveButton("是") { _, _ ->
                        Urls.clearUrl(this)
                        updateHistory()
                    }.setNegativeButton("否", null)
                    .create().show()
        }
        printBtn.click {
            PrintTestAct.startActivity(this)
        }
        changeScreenOrientationBtn.click {
            changeScreenOrientation()
        }
        webTestBtn.click {
//            QRCodeAct.startActivityForResult(this)
//            WebViewAct.startActivity(this, "https://2fmlee.axshare.com/#g=1&p=%E5%9B%9B%E7%B1%BB%E8%A1%8C%E4%B8%9A%E8%81%94%E5%90%88%E8%A1%8C%E6%94%BF%E6%8C%87%E5%AF%BC%E6%9C%8D%E5%8A%A1%E5%B9%B3%E5%8F%B0%EF%BC%88app%EF%BC%89")
//            WebViewAct.startActivity(this, "file:///android_asset/demo.html")
//            WebViewAct.startActivity(this, "file:///android_asset/web_test.html")
            TbsOfficeReaderAct.startActivity(this, "xxxx", "http://47.93.32.132:8080//sjj_upload/word/20200319/20200319140305_599.xml.docx")
        }

        upgradeBtn.click {
            UpgradeAct.startActivity(this)
//            TransparentAct.startActivity(this)
        }

        updateHistory()
    }

    private fun updateHistory() {
        Observable.create<List<String>> { emitter ->
            emitter.onNext(Urls.getUrl(this))
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<String>> {
                    override fun onSubscribe(d: Disposable) {
                        historyAdapter?.showLoading()
                    }

                    override fun onNext(urls: List<String>) {
                        historyAdapter?.showEmpty()
                        historyAdapter?.setNewData(urls)
                    }

                    override fun onError(e: Throwable) {
                        historyAdapter?.showEmpty()
                    }

                    override fun onComplete() {}
                })
    }

    override fun initLayout(): Int {
        return R.layout.act_main
    }

    override fun overStatusBar(): Boolean {
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPrintResultEvent(event: PrintResultEvent) {
        if (event.success && this.localClassName == event.className) {
            LogUtils.d("MainAct 打印成功")
            ToastUtil.shortToast(this, event.content ?: "")
        }
    }

}