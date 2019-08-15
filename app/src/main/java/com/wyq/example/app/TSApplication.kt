package com.wyq.example.app

import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.wyq.base.BaseApplication

/**
 * Created by wyq
 * Date: 2018/12/21
 */
class TSApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()

        SDKInitializer.initialize(this)
        SDKInitializer.setCoordType(CoordType.BD09LL)
    }
}