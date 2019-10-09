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

    public fun underLine(): SignPrint {
        this.underLine = true
        return this
    }

    public fun bold(): SignPrint {
        this.bold = true
        return this
    }

    public fun hideSignTip(): SignPrint {
        this.showSignTip = false
        return this
    }

}