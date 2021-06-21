package com.ts.base.office

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ts.base.R
import com.ts.base.activity.BaseActivity
import kotlinx.android.synthetic.main.base_act_office_reader.*

/**
 * GOOGLE、微软转码方式浏览文档
 */
class OfficeReaderAct: BaseActivity() {

    companion object {
        fun startActivity(context: Context, name: String, url: String) {
            val intent = Intent(context, OfficeReaderAct::class.java)
            intent.putExtra("name", name)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    private var name: String? = null
    private var url: String? = null

    override fun initData(intent: Intent) {
        super.initData(intent)

        name = intent.getStringExtra("name")
        url = intent.getStringExtra("url")
//        url = "https://contents.netbk.co.jp/pc/pdf/pr/20150130_fs.pdf"
        url = "http://106.14.184.16/fsspv2-1.0/common/pdfViewPage.action?pdfUrl=/sjj_upload/word/20190122/20190122133716_122.xml.docx.pdf"
//        url = "http://106.14.184.16/sjj_upload/123.docx"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        super.initViews()

        titleTv?.text = name
        //使用微软打开，在线预览
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        /**
         * 加载完成后才关闭加载框
         */
        webView.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$url") // 使用google在线浏览方式
//        webView.loadUrl("https://view.officeapps.live.com/op/view.aspx?src=$url") // 使用微软在线浏览方式
//        webView.loadUrl(url)
    }

    override fun initLayout(): Int {
        return R.layout.base_act_office_reader
    }

    override fun overStatusBar(): Boolean {
        return false
    }
}