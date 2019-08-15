package com.wyq.base.printer.bean

import com.wyq.base.printer.two.Printer_define
import com.wyq.base.printer.two.esc.ESC

/**
 * json解析的bean
 */
class PrintBean {
    var type: String = BasePrint.Type.TEXT.value

    var blankHeight: Int = 0 // 空白高度

    var lineTip: String? = null // 分割线上部显示的内容

    var signTip: String? = null // 签名前面的提示
    var signPath: String? = null // 签名图片路径

    var stamp: String? = null // 印章

    var text: String? = null // 打印内容
    var underLine: Boolean = false // 是否带下划线
    var bold: Boolean = false // 是否加粗
    var textSize: Int = BasePrint.TextSize.X24.value // 字体大小
    var align: String = BasePrint.Align.LEFT.value // 对齐方式

    public fun revertTextSize(): ESC.FONT_HEIGHT {
        return when(textSize) {
            BasePrint.TextSize.X16.value -> ESC.FONT_HEIGHT.x16
            BasePrint.TextSize.X24.value -> ESC.FONT_HEIGHT.x24
            BasePrint.TextSize.X32.value -> ESC.FONT_HEIGHT.x32
            BasePrint.TextSize.X48.value -> ESC.FONT_HEIGHT.x48
            BasePrint.TextSize.X64.value -> ESC.FONT_HEIGHT.x64
            else -> ESC.FONT_HEIGHT.x24
        }
    }

    public fun revertAlign(): Printer_define.ALIGN {
        return when(align) {
            BasePrint.Align.LEFT.value -> Printer_define.ALIGN.LEFT
            BasePrint.Align.CENTER.value -> Printer_define.ALIGN.CENTER
            BasePrint.Align.RIGHT.value -> Printer_define.ALIGN.RIGHT
            else -> Printer_define.ALIGN.LEFT
        }
    }

}