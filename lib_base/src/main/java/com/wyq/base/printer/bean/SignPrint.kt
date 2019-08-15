package com.wyq.base.printer.bean

/**
 * 签名
 */
class SignPrint(var signTip: String?, var signPath: String?) : BasePrint(BasePrint.Type.SIGNATURE.value) {
    constructor(imgPath: String?): this(null, imgPath)
}