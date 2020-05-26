package com.wyq.base.printer.bean

/**
 * @param type 类型
 */
open class BasePrint(var type: String) {

    enum class Type(var value: String) {
        TEXT("text"),
        LINE("line"),
        BLANK("blank"),
        ENTER("enter"),
        STAMP("stamp"),
        SIGNATURE("signature"),
        QRCODE("qrcode")
    }

    enum class TextSize(var value: Int) {
        X16(0),
        X24(1),
        X32(2),
        X48(3),
        X64(4)
    }

    enum class Align(var value: String) {
        LEFT("left"),
        RIGHT("right"),
        CENTER("center")
    }
}