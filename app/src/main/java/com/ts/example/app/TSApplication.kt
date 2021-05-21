package com.ts.example.app

import com.ts.base.BaseApplication
//import com.ts.base.constant.BlueToothConnectWay
//import com.ts.base.printer.PrintConfig

/**
 * Created by ts
 * Date: 2018/12/21
 */
class TSApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()

//        PrintConfig.blueToothConnectWay = BlueToothConnectWay.TYPE_ESC
    }
}