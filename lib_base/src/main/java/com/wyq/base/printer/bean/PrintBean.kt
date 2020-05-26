package com.wyq.base.printer.bean

import com.wyq.base.BaseActivity
import com.wyq.base.printer.jqPrinter.Printer_define
import com.wyq.base.printer.jqPrinter.esc.ESC

/**
 * json解析的bean
 */
class PrintBean {
    var type: String = BasePrint.Type.TEXT.value

    var blankHeight: Int = 0 // 空白高度

    var lineTip: String? = null // 分割线上部显示的内容

    var signTip: String? = null // 签名前面的提示
    var signPath: String? = null // 签名图片路径
    var signWidth: Int = BaseActivity.SIGNATURE_WIDTH // 签名大小
    var showSignTip: Boolean = true // 是否打印提示

    var stamp: String? = null // 印章

    var text: String? = null // 打印内容
    var underLine: Boolean = false // 是否带下划线
    var bold: Boolean = false // 是否加粗
    var enter: Boolean = false // 是否换行
    var textSize: Int = BasePrint.TextSize.X24.value // 字体大小
    var align: String = BasePrint.Align.LEFT.value // 对齐方式

    var startX: Int = 0 // 二维码起始位置
    var startY: Int = 0 // 二维码起始位置

    public fun getTextSizeHeight(): Int {
        return when(textSize) {
            BasePrint.TextSize.X16.value -> 16
            BasePrint.TextSize.X24.value -> 24
            BasePrint.TextSize.X32.value -> 32
            BasePrint.TextSize.X48.value -> 48
            BasePrint.TextSize.X64.value -> 64
            else -> 24
        }
    }

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