package com.ts.base.printer.bean

/**
 * 分割线
 */
class LinePrint(var lineTip: String?) : BasePrint(BasePrint.Type.LINE.value) {
    constructor(): this(null)
}