package com.wyq.example.app

import com.wyq.base.BaseApplication
//import com.wyq.base.constant.BlueToothConnectWay
//import com.wyq.base.printer.PrintConfig

/**
 * Created by wyq
 * Date: 2018/12/21
 */
class TSApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()

//        PrintConfig.blueToothConnectWay = BlueToothConnectWay.TYPE_ESC
    }
}