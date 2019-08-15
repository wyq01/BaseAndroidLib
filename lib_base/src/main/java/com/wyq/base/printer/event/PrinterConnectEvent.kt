package com.wyq.base.printer.event

/**
 * Created by wyq
 * Date: 2019/1/3
 */
class PrinterConnectEvent(val status: Int) {
    companion object {
        const val STATUS_SUCCESS = 2001
        const val STATUS_FAILED = 2002
        const val STATUS_CONNECTING = 2003
        const val STATUS_CONNECTED = 2004
    }
}