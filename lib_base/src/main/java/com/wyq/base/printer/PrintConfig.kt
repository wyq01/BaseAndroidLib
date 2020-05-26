package com.wyq.base.printer

import android.app.Activity
import com.blankj.utilcode.util.ImageUtils
import com.wyq.base.BaseApplication
import com.wyq.base.R

import com.wyq.base.printer.jqPrinter.Printer_define
import com.wyq.base.printer.jqPrinter.esc.ESC
import com.wyq.base.sign.util.BitmapUtil

/**
 * Created by wyq
 * Date: 2019/1/4
 */
object PrintConfig {

    fun testPrint(activity: Activity) {
        val printer = (activity.application as BaseApplication).getJQPrinter()
        if (printer != null && printer.canPrint()) {
            val str = ("被监督人：姑苏区陆二私房菜馆\n"
                    + "法定代表人（负责人）：陆叶君\n"
                    + "地    址：江苏省苏州市姑苏区双塔街道凤凰街390号\n"
                    + "联系电话：13800000000\n"
                    + "监督意见：\n\n禁止生产经营《食品安全法》第三十四条所列的禁止生产经营的食品、食品添加剂、食品相关产品。\n\n"
                    + "以上意见限于 日内改正。")
            //第一份
            printer.esc.text.printOut(
                Printer_define.ALIGN.CENTER,
                ESC.FONT_HEIGHT.x32,
                true,
                ESC.TEXT_ENLARGE.NORMAL,
                "监督意见书"
            )
            printer.esc.feedEnter()
            printer.esc.text.printOut(
                Printer_define.ALIGN.CENTER,
                ESC.FONT_HEIGHT.x24,
                true,
                ESC.TEXT_ENLARGE.NORMAL,
                "第" + 2019000001 + "号"
            )
            printer.esc.feedDots(20)
            printer.esc.feedEnter()
            printer.esc.text.printOut(str)
            printer.esc.feedDots(200)
            printer.esc.text.setBold(true)
            printer.esc.text.drawOut(0, 16, "当事人签收:")
            printer.esc.text.drawOut(360, 16, "(公章)")
            printer.esc.feedEnter()
            printer.esc.feedDots(50)
            printer.esc.text.drawOut(0, 16, "    年    月    日")
            printer.esc.text.drawOut(360, 16, "2019年1月1日")
            printer.esc.feedEnter()
            printer.esc.feedDots(200)

            //下划线
            val lines = arrayOfNulls<ESC.LINE_POINT>(1)
            lines[0] = ESC.LINE_POINT(0, 575)
            printer.esc.text.setBold(false)
            printer.esc.text.printOut("请沿此处撕开")
            for (i in 0..3) {
                printer.esc.graphic.linedrawOut(lines)
            }
            printer.esc.feedDots(4)

            //第二份
            printer.esc.feedDots(20)
            printer.esc.feedEnter()
            printer.esc.text.printOut(
                Printer_define.ALIGN.CENTER,
                ESC.FONT_HEIGHT.x32,
                true,
                ESC.TEXT_ENLARGE.NORMAL,
                "监督意见书"
            )
            printer.esc.feedEnter()
            printer.esc.text.printOut(
                Printer_define.ALIGN.CENTER,
                ESC.FONT_HEIGHT.x24,
                true,
                ESC.TEXT_ENLARGE.NORMAL,
                "第" + 2019000001 + "号"
            )
            printer.esc.feedDots(20)
            printer.esc.feedEnter()
            printer.esc.text.printOut(str)
            printer.esc.feedDots(200)
            printer.esc.text.setBold(true)
            printer.esc.text.drawOut(0, 16, "当事人签收:")
            printer.esc.text.drawOut(360, 16, "(公章)")
            printer.esc.feedEnter()
            printer.esc.feedDots(50)
            printer.esc.text.drawOut(0, 16, "    年    月    日")
            printer.esc.text.drawOut(360, 16, "2019年1月1日")

            printer.esc.feedDots(50)
            printer.esc.text.setBold(false)
            val rectStartX = 125
            val rect = BitmapUtil.zoomImg(ImageUtils.getBitmap(R.drawable.base_ic_stamp_rect), 320)
            printer.esc.image.drawOut(rectStartX, 0, rect)
            val startX = rectStartX + 16
            var stamp = "白洋湾街道"
            printer.esc.text.drawOut(startX + 0, (rect.height * 0.7f).toInt(), ESC.FONT_HEIGHT.x32, ESC.TEXT_ENLARGE.NORMAL, "姑苏区“四类行业”")
            printer.esc.text.drawOut(startX + 80, (rect.height * 0.42f).toInt(), ESC.FONT_HEIGHT.x32, ESC.TEXT_ENLARGE.NORMAL, "整治专班")
            printer.esc.text.drawOut(startX + 144 - stamp.length * 12, (rect.height * 0.1f).toInt(), ESC.FONT_HEIGHT.x24, ESC.TEXT_ENLARGE.NORMAL, stamp)
            printer.esc.feedDots(100)
        } else {
            (activity.application as BaseApplication).setDeviceAddress("")
            PrinterConnectAct.startActivity(activity)
        }
    }

}