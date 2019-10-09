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
import com.wyq.base.util.DateFormatUtil
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
        val headerBean = TextPrint("苏州市姑苏区市场监督管理局")
            .textSizeX24()
            .alignCenter()
            .bold()
        val titleBean = TextPrint("责令整改通知书")
            .textSizeX32()
            .alignCenter()
            .bold()
        val numberBean = TextPrint("市监〔1296〕号")
            .alignCenter()
            .bold()
        val unitBean = TextPrint("苏州泰克赛威网络科技有限公司")
            .bold()
            .underLine()
        val tip1 = TextPrint("　　经查，你（单位）")
        val problemBean = TextPrint("aaaaaaaaaaaaaaaaaaaaaaaaaa")
            .underLine()
        val tip2 = TextPrint("的行为，违反了")
        val provisionBean = TextPrint("bbbbbbbbbbbbbbbbbbbbbbbbbb")
            .underLine()
        val tip3 = TextPrint("的规定。依据")
        val lawBean = TextPrint("ccccccccccccccccccccccccccccc")
            .underLine()
        val tip4 = TextPrint("的规定，现责令你（单位）在")
        val changeTimeBean = TextPrint("2019年10月1日")
            .underLine()
        val tip5 = TextPrint("前改正。\n　　逾期不改的，本局将依据")
        val provisionBean2 = TextPrint("dddddddddddddddddddddddddddddddd")
            .underLine()
        val tip6 = TextPrint("的规定，")
        val handleBean = TextPrint("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
            .underLine()
        val tip7 = TextPrint("改正内容及要求：")
        val changeContentBean = TextPrint("")
            .underLine()
        val tip8 = TextPrint("\n　　如对本责令改正决定不服，可以自收到本通知书之日起六日内向")
        val cityBean = TextPrint("姑苏区")
            .underLine()
        val tip10 = TextPrint("人民政府或者")
        val unit1Bean = TextPrint("苏州市食品药品监督管理局")
            .underLine()
        val tip11 = TextPrint("申请行政复议；也可以在六个月内依法向")
        val unit2Bean = TextPrint("苏州市")
            .underLine()
        val tip12 = TextPrint("人民法院提起行政诉讼。")
//        val enforceSignBean = SignPrint("联系人（签名/盖章）", enforce)
//            .bold(true)
        val tip13 = TextPrint("联系电话：")
        val phoneBean = TextPrint("0512-66666666")
            .underLine()
        val dateBean = TextPrint(DateFormatUtil.format(DateFormatUtil.yyyyYMYdR))
            .alignRight()

        list.add(BlankPrint(80))
        list.add(headerBean)
        list.add(titleBean)
        list.add(numberBean)
        list.add(BlankPrint(50))
        list.add(unitBean)
        list.add(EnterPrint())
        list.add(tip1)
        list.add(problemBean)
        list.add(tip2)
        list.add(provisionBean)
        list.add(tip3)
        list.add(lawBean)
        list.add(tip4)
        list.add(changeTimeBean)
        list.add(tip5)
        list.add(provisionBean2)
        list.add(tip6)
        list.add(handleBean)
        list.add(tip7)
        list.add(changeContentBean)
        list.add(tip8)
        list.add(cityBean)
        list.add(tip10)
        list.add(unit1Bean)
        list.add(tip11)
        list.add(unit2Bean)
        list.add(tip12)
        list.add(EnterPrint())
        list.add(tip13)
        list.add(phoneBean)
        list.add(EnterPrint())
        list.add(dateBean)
        list.add(BlankPrint(50))

        list.add(LinePrint())

        list.add(BlankPrint(80))
        list.add(headerBean)
        list.add(titleBean)
        list.add(numberBean)
        list.add(BlankPrint(50))
        list.add(unitBean)
        list.add(EnterPrint())
        list.add(tip1)
        list.add(problemBean)
        list.add(tip2)
        list.add(provisionBean)
        list.add(tip3)
        list.add(lawBean)
        list.add(tip4)
        list.add(changeTimeBean)
        list.add(tip5)
        list.add(provisionBean2)
        list.add(tip6)
        list.add(handleBean)
        list.add(tip7)
        list.add(changeContentBean)
        list.add(tip8)
        list.add(cityBean)
        list.add(tip10)
        list.add(unit1Bean)
        list.add(tip11)
        list.add(unit2Bean)
        list.add(tip12)
        list.add(EnterPrint())
        list.add(tip13)
        list.add(phoneBean)
        list.add(EnterPrint())
        list.add(dateBean)
        list.add(BlankPrint(50))

        initJson()
    }

    private fun test2() {
        list.clear()
        val headerBean = TextPrint(header)
            .alignCenter()
            .textSizeX32()
            .bold()

        val titleBean = TextPrint(title)
            .alignCenter()
            .textSizeX24()
            .bold()

        val contentBean = TextPrint(content)

        val dateBean = TextPrint(date)
            .alignRight()

        list.add(BlankPrint(20))
        list.add(headerBean)
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(100))
        list.add(headerBean)
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(50))

        list.add(LinePrint())

        list.add(BlankPrint(50))
        list.add(headerBean)
        list.add(titleBean)
        list.add(BlankPrint(20))
        list.add(contentBean)
        list.add(BlankPrint(100))
        list.add(headerBean)
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
        val t1 = TextPrint("苏州xxxx有限公司：")
            .textSizeX32()
            .bold()
        val t2 = TextPrint("因你公司违反了xxxx规定，现对你司进行如下处罚：")
        val t3 = TextPrint("    从今日起至")
        val t4 = TextPrint("2020年1月1日")
            .textSizeX32()
            .bold()
            .underLine()
        val t5 = TextPrint("不得开门营业，直到整改结束。")

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
                            .textSizeX32()
                            .bold()
                            .underLine()
                            .hideSignTip()
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