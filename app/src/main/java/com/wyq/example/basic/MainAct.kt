package com.wyq.example.basic

import android.support.v7.widget.LinearLayoutManager
import com.wyq.example.R
import com.wyq.example.basic.adapter.HistoryAdapter
import com.wyq.example.basic.util.Urls
import com.wyq.base.BaseActivity
import com.wyq.base.printer.event.PrintResultEvent
import com.wyq.base.util.LogUtil
import com.wyq.base.util.ToastUtil
import com.wyq.base.util.click
import com.wyq.base.view.BaseDialog
import com.wyq.base.view.VerticalDividerWithoutTB
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
                resources.getColor(R.color.light_divider)
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
            WebViewAct.startActivity(this, "file:///android_asset/web_test.html")
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
            LogUtil.d("MainAct 打印成功")
            ToastUtil.shortToast(this, event.content ?: "")
        }
    }

}