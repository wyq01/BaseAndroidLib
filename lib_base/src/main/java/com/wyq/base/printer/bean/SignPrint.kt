package com.wyq.base.printer.bean

/**
 * 签名
 */
class SignPrint(var signTip: String?, var signPath: String?) : BasePrint(BasePrint.Type.SIGNATURE.value) {
    constructor(imgPath: String?): this(null, imgPath)
    var textSize: Int = BasePrint.TextSize.X24.value // 字体大小，默认为24
    var underLine: Boolean = false // 是否带下划线
    var bold: Boolean = false // 是否加粗

    public fun textSize(textSize: Int): SignPrint {
        this.textSize = textSize
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

}