package com.wyq.base.printer.bean

/**
 * 文本
 */
class TextPrint : BasePrint(BasePrint.Type.TEXT.value) {
    var text: String? = null // 打印内容
    var textSize: Int = BasePrint.TextSize.X24.value // 字体大小，默认为24
    var underLine: Boolean = false // 是否带下划线
    var bold: Boolean = false // 是否加粗
    var align: String = BasePrint.Align.LEFT.value // 对齐方式
}