package com.wyq.base.printer.bean

/**
 * @param text 打印内容
 * 文本
 */
class TextPrint(var text: String?) : BasePrint(BasePrint.Type.TEXT.value) {
    var textSize: Int = BasePrint.TextSize.X24.value // 字体大小，默认为24
    var underLine: Boolean = false // 是否带下划线
    var bold: Boolean = false // 是否加粗
    var align: String = BasePrint.Align.LEFT.value // 对齐方式

    public fun textSize(textSize: Int): TextPrint {
        this.textSize = textSize
        return this
    }

    public fun underLine(underLine: Boolean): TextPrint {
        this.underLine = underLine
        return this
    }

    public fun bold(bold: Boolean): TextPrint {
        this.bold = bold
        return this
    }

    public fun align(align: String): TextPrint {
        this.align = align
        return this
    }

}