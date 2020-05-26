package com.wyq.base.printer.bean

/**
 * 签名
 */
class QRCodePrint(var text: String?) : BasePrint(BasePrint.Type.QRCODE.value) {
    private var align: String = BasePrint.Align.LEFT.value // 对齐方式
    private var startX: Int = 0 // 只有左对齐时有用
    private var startY: Int = 0 // 只有左对齐时有用

    public fun startX(startX: Int): QRCodePrint {
        this.startX = startX
        return this
    }

    public fun startY(startY: Int): QRCodePrint {
        this.startY = startY
        return this
    }

    public fun position(startX: Int, startY: Int) : QRCodePrint {
        this.startX = startX
        this.startY = startY
        return this
    }

    public fun alignCenter(): QRCodePrint {
        this.align = BasePrint.Align.CENTER.value
        return this
    }

    public fun alignLeft(): QRCodePrint {
        this.align = BasePrint.Align.LEFT.value
        return this
    }

    public fun alignRight(): QRCodePrint {
        this.align = BasePrint.Align.RIGHT.value
        return this
    }
}