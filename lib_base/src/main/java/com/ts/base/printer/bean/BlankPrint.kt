package com.ts.base.printer.bean

/**
 * 空白走纸
 */
class BlankPrint(var blankHeight: Int = 0) : BasePrint(BasePrint.Type.BLANK.value)