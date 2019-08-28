package com.wyq.base.printer.one

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.wyq.base.BaseApplication
import com.wyq.base.printer.BluetoothUtil
import com.wyq.base.printer.event.BluetoothStatusEvent
import com.wyq.base.printer.event.PrinterConnectEvent
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

/**
 * 第一种打印机打印方式
 */
class PrinterConnectService1 : Service() {
    private var bluetoothSocket: BluetoothSocket? = null

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Deprecated("")
    override fun onStart(intent: Intent, startId: Int) {
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val deviceAddress = intent.getStringExtra(BluetoothUtil.BLUETOOTH_ADDRESS)
        if (!TextUtils.isEmpty(deviceAddress)) {
            autoConnect(deviceAddress)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        closeSocket()
    }

    private fun closeSocket() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket?.close()
                bluetoothSocket = null
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun autoConnect(deviceAddress: String?) {
        if (bluetoothSocket != null) {
            if (bluetoothSocket?.remoteDevice?.address == deviceAddress) {
                EventBus.getDefault().post(PrinterConnectEvent(PrinterConnectEvent.STATUS_CONNECTED))
                LogUtils.d("打印机已连接")
            } else {
                closeSocket()
                connect(deviceAddress)
            }
        } else {
            connect(deviceAddress)
        }
    }

    private fun connect(deviceAddress: String?) {
        Observable.create(ObservableOnSubscribe<BluetoothSocket> { emitter ->
            val socket = BluetoothUtil.connectDevice(deviceAddress)
            when {
                socket != null -> emitter.onNext(socket)
                else -> emitter.onError(Throwable())
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BluetoothSocket> {
                override fun onSubscribe(d: Disposable) {
                    EventBus.getDefault().post(PrinterConnectEvent(PrinterConnectEvent.STATUS_CONNECTING))
                }
                override fun onNext(result: BluetoothSocket) {
                    try {
                        LogUtils.d("打印机连接成功")
                        bluetoothSocket = result
                        (application as BaseApplication).setPrinter(Printer(bluetoothSocket?.outputStream, "GBK"))
                        (application as BaseApplication).setDeviceAddress(deviceAddress)
                        EventBus.getDefault().post(PrinterConnectEvent(PrinterConnectEvent.STATUS_SUCCESS))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        EventBus.getDefault().post(PrinterConnectEvent(PrinterConnectEvent.STATUS_FAILED))
                        LogUtils.d("打印机连接失败")
                    }
                }
                override fun onError(e: Throwable) {
                    EventBus.getDefault().post(PrinterConnectEvent(PrinterConnectEvent.STATUS_FAILED))
                    LogUtils.d("打印机连接失败")
                }
                override fun onComplete() {}
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBluetoothStatusEvent(event: BluetoothStatusEvent) {
        when (event.status) {
            BluetoothAdapter.STATE_OFF // 蓝牙关闭
                , BluetoothAdapter.STATE_DISCONNECTED // 蓝牙断开连接
            -> closeSocket()
            BluetoothAdapter.STATE_ON // 蓝牙打开后重新连接
            -> autoConnect((application as BaseApplication).getDeviceAddress())
        }
    }

}