package com.wyq.base.printer.bean

/**
 * 签名
 */
class SignPrint(var signTip: String?, var signPath: String?) : BasePrint(BasePrint.Type.SIGNATURE.value) {
    constructor(imgPath: String?): this(null, imgPath)
    private var textSize: Int = BasePrint.TextSize.X24.value // 字体大小，默认为24
    private var underLine: Boolean = false // 是否带下划线
    private var bold: Boolean = false // 是否加粗
    private var showSignTip: Boolean = true // 是否打印提示

    public fun textSize(textSize: Int): SignPrint {
        this.textSize = textSize
        return this
    }

    public fun textSizeX16(): SignPrint {
        this.textSize = BasePrint.TextSize.X16.value
        return this
    }

    public fun textSizeX24(): SignPrint {
        this.textSize = BasePrint.TextSize.X24.value
        return this
    }

    public fun textSizeX32(): SignPrint {
        this.textSize = BasePrint.TextSize.X32.value
        return this
    }

    public fun textSizeX48(): SignPrint {
        this.textSize = BasePrint.TextSize.X48.value
        return this
    }

    public fun textSizeX64(): SignPrint {
        this.textSize = BasePrint.TextSize.X64.value
        return this
    }

    public fun underLine(underLine: Boolean): SignPrint {
        this.underLine = underLine
        return this
    }

    public fun bold(bold: Boolean): SignPrint {
        this.bold = bold
        return this
    }

    public fun showSignTip(showSignTip: Boolean): SignPrint {
        this.showSignTip = showSignTip
        return this
    }

}