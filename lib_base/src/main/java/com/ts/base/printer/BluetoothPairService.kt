package com.ts.base.printer

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

import com.ts.base.printer.receiver.BluetoothReceiver

/**
 * 监听蓝牙设备绑定和解绑
 * Created by ts
 */
class BluetoothPairService : Service() {

    private var receiver: BluetoothReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        receiver = BluetoothReceiver()

        val intent = IntentFilter()
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED) // 设备连接状态改变的广播
        registerReceiver(receiver, intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

}