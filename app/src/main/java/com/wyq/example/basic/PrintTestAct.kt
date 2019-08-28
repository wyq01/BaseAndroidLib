package com.wyq.example.basic

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.wyq.base.BaseActivity
import com.wyq.base.printer.PrinterConnectAct
import com.wyq.base.printer.bean.*
import com.wyq.base.printer.event.PrintResultEvent
import com.wyq.base.sign.SignActivity
import com.wyq.base.sign.config.PenConfig
import com.wyq.base.util.ToastUtil
import com.wyq.base.util.click
import com.wyq.example.R
import kotlinx.android.synthetic.main.act_print_test.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PrintTestAct : BaseActivity() {

    private val header = "监督意见书"
    private val title = "第2019000001号"
    private val content = "被监督人：姑苏区陆二私房菜馆\n法定代表人（负责人）：陆叶君\n地    址：江苏省苏州市姑苏区双塔街道凤凰街390号\n联系电话：13800000000\n监督意见：\n\n禁止生产经营《食品安全法》第三十四条所列的禁止生产经营的食品、食品添加剂、食品相关产品。\n\n以上意见限于 日内改正。"
    private val twice = true
    private val stamp = "白洋湾街道"
    private val date = "2019-08-05"

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, PrintTestAct::class.java)
            context.startActivity(intent)
        }
    }

    private val list = ArrayList<BasePrint>()

    override fun initViews() {
        super.initViews()

        oldTv.setText("header = $header\ntitle = $title\ncontent = $content\nstamp = $stamp\ntwice = $twice")
        oldPrintBtn.click {
            print(header, title, content, stamp, twice)
        }

        initJson()

        print1Btn.click {
            test1()
            print(newTv.text.toString())
        }

        print2Btn.click {
            test2()
            print(newTv.text.toString())
        }

        print3Btn.click {
            test3()
            print(newTv.text.toString())
        }

        print4Btn.click {
            print(newTv.text.toString())
        }

        signBtn.click {
            SignActivity.startActivityForResult(this)
        }

        connectBtn.click {
            PrinterConnectAct.startActivity(this)
        }
    }

    private fun test1() {
        list.clear()
        val headerBean = TextPrint()
        headerBean.text = header
        headerBean.align = BasePrint.Align.CENTER.value
        headerBean.textSize = BasePrint.TextSize.X32.value
        headerBean.bold = true

        val titleBean = TextPrint()
        titleBean.text = title
        titleBean.align = BasePrint.Align.CENTER.value
        titleBean.textSize = BasePrint.TextSize.X24.value
        titleBean.bold = true

        val contentBean = TextPrint()
        contentBean.text = content

        list.add(BlankPrint(20))
        list.add(headerBean)
        list.add(BlankPrint(10))
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(50))
        list.add(StampPrint(stamp))
        list.add(BlankPrint(50))
        list.add(LinePrint())
        list.add(BlankPrint(50))
        list.add(headerBean)
        list.add(BlankPrint(10))
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(50))
        list.add(StampPrint(stamp))
        list.add(BlankPrint(50))

        initJson()
    }

    private fun test2() {
        list.clear()
        val headerBean = TextPrint()
        headerBean.text = header
        headerBean.align = BasePrint.Align.CENTER.value
        headerBean.textSize = BasePrint.TextSize.X32.value
        headerBean.bold = true

        val titleBean = TextPrint()
        titleBean.text = title
        titleBean.align = BasePrint.Align.CENTER.value
        titleBean.textSize = BasePrint.TextSize.X24.value
        titleBean.bold = true

        val contentBean = TextPrint()
        contentBean.text = content

        val dateBean = TextPrint()
        dateBean.text = date
        dateBean.align = BasePrint.Align.RIGHT.value

        list.add(BlankPrint(20))
        list.add(headerBean)
        list.add(EnterPrint())
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(100))
        list.add(headerBean)
        list.add(BlankPrint(10))
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(50))

        list.add(LinePrint())

        list.add(BlankPrint(50))
        list.add(headerBean)
        list.add(EnterPrint())
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(100))
        list.add(headerBean)
        list.add(BlankPrint(10))
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(50))

        list.add(dateBean)
        list.add(BlankPrint(50))

        initJson()
    }

    private fun test3() {
        list.clear()
        val t1 = TextPrint()
        t1.text = "苏州xxxx有限公司："
        t1.bold = true
        t1.textSize = BasePrint.TextSize.X32.value

        val t2 = TextPrint()
        t2.text = "因你公司违反了xxxx规定，现对你司进行如下处罚："

        val t3 = TextPrint()
        t3.text = "    从今日起至"

        val t4 = TextPrint()
        t4.text = "2020年1月1日"
        t4.textSize = BasePrint.TextSize.X32.value
        t4.bold = true
        t4.underLine = true

        val t5 = TextPrint()
        t5.text = "不得开门营业，直到整改结束。"

        list.add(BlankPrint(20))
        list.add(t1)
        list.add(BlankPrint(20))
        list.add(t2)
        list.add(BlankPrint(20))
        list.add(t3)
        list.add(t4)
        list.add(t5)
        list.add(BlankPrint(100))

        initJson()
    }

    private fun initJson() {
        val json = Gson().toJson(list)
        var jsonStr = json.replace("[", "[\n")
        jsonStr = jsonStr.replace("]", "\n]")
        jsonStr = jsonStr.replace("{", "{\n")
        jsonStr = jsonStr.replace("}", "\n}")
        jsonStr = jsonStr.replace(",", ",\n")

        newTv.setText(jsonStr)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SignActivity.REQUEST_SIGN -> {
                    data?.let {
                        val path = it.getStringExtra(PenConfig.SAVE_PATH)
                        val signBean = SignPrint("打印人员签名", path)
                        signBean.textSize = BasePrint.TextSize.X32.value
                        signBean.bold = true
                        signBean.underLine = true
                        list.add(signBean)
                        list.add(BlankPrint(50))
                        initJson()
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPrintResultEvent(event: PrintResultEvent) {
        if (event.success && this.localClassName == event.className) {
            LogUtils.d("PrintTestAct 打印成功")
            ToastUtil.shortToast(this, event.content ?: "")
        }
    }

    override fun initLayout(): Int {
        return R.layout.act_print_test
    }

    override fun overStatusBar(): Boolean {
        return false
    }

}