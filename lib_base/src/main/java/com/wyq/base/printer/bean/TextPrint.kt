package com.wyq.base.printer.bean

/**
 * @param text 打印内容
 * 文本
 */
class TextPrint(var text: String?) : BasePrint(BasePrint.Type.TEXT.value) {
    private var textSize: Int = BasePrint.TextSize.X24.value // 字体大小，默认为24
    private var underLine: Boolean = false // 是否带下划线
    private var bold: Boolean = false // 是否加粗
    private var align: String = BasePrint.Align.LEFT.value // 对齐方式

    public fun textSizeX16(): TextPrint {
        this.textSize = BasePrint.TextSize.X16.value
        return this
    }

    public fun textSizeX24(): TextPrint {
        this.textSize = BasePrint.TextSize.X24.value
        return this
    }

    public fun textSizeX32(): TextPrint {
        this.textSize = BasePrint.TextSize.X32.value
        return this
    }

    public fun textSizeX48(): TextPrint {
        this.textSize = BasePrint.TextSize.X48.value
        return this
    }

    public fun textSizeX64(): TextPrint {
        this.textSize = BasePrint.TextSize.X64.value
        return this
    }

    public fun underLine(): TextPrint {
        this.underLine = true
        return this
    }

    public fun bold(): TextPrint {
        this.bold = true
        return this
    }

    public fun alignCenter(): TextPrint {
        this.align = BasePrint.Align.CENTER.value
        return this
    }

    public fun alignLeft(): TextPrint {
        this.align = BasePrint.Align.LEFT.value
        return this
    }

    public fun alignRight(): TextPrint {
        this.align = BasePrint.Align.RIGHT.value
        return this
    }

}