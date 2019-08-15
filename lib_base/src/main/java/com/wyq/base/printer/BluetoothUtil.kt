package com.wyq.base.printer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import com.wyq.base.constant.RequestCode

import java.io.IOException
import java.util.ArrayList
import java.util.UUID

object BluetoothUtil {

    const val BLUETOOTH_ADDRESS = "bluetooth_address"

    //	public static void saveBluetoothDeviceAddress(Context context, String deviceAddress) {
    //		SPUtil.put(context, BLUETOOTH_ADDRESS, deviceAddress);
    //	}
    //
    //	public static String getBluetoothDeviceAddress(Context context) {
    //		return (String) SPUtil.get(context, BLUETOOTH_ADDRESS, "");
    //	}
    //
    //	public static void clearBluetoothDeviceAddress(Context context) {
    //		SPUtil.remove(context, BLUETOOTH_ADDRESS);
    //	}

    /**
     * 蓝牙是否打开
     */
    // 蓝牙已打开
    val isBluetoothOn: Boolean
        get() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled)
                return true
            return false
        }

    /**
     * 获取所有已配对的设备
     */
    val pairedDevices: List<BluetoothDevice>
        get() {
            val deviceList = ArrayList<BluetoothDevice>()
            val pairedDevices = BluetoothAdapter.getDefaultAdapter().bondedDevices
            if (pairedDevices.size > 0) {
                for (device in pairedDevices) {
                    deviceList.add(device)
                }
            }
            return deviceList
        }

    /**
     * 获取所有已配对的打印类设备
     */
    val pairedPrinterDevices: List<BluetoothDevice>
        get() = getSpecificDevice(BluetoothClass.Device.Major.IMAGING)

    /**
     * 从已配对设配中，删选出某一特定类型的设备展示
     * @param deviceClass
     * @return
     */
    fun getSpecificDevice(deviceClass: Int): List<BluetoothDevice> {
        val devices = pairedDevices
        val printerDevices = ArrayList<BluetoothDevice>()

        for (device in devices) {
            val klass = device.bluetoothClass
            // 关于蓝牙设备分类参考 http://stackoverflow.com/q/23273355/4242112
            if (klass.majorDeviceClass == deviceClass)
                printerDevices.add(device)
        }

        return printerDevices
    }

    /**
     * 弹出系统对话框，请求打开蓝牙
     */
    fun openBluetooth(activity: Activity) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBtIntent, RequestCode.REQUEST_BLUETOOTH)
    }

    //	public static BluetoothSocket connectDevice(Context context) {
    //		String bluetoothAddress = getBluetoothDeviceAddress(context);
    //		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddress);
    //		return connectDevice(device);
    //	}

    fun connectDevice(deviceAddress: String?): BluetoothSocket? {
        val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
        return connectDevice(device)
    }

    fun connectDevice(device: BluetoothDevice): BluetoothSocket? {
        var socket: BluetoothSocket? = null
        try {
            socket = device.createRfcommSocketToServiceRecord(
                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            )
            socket?.connect()
        } catch (e: IOException) {
            try {
                socket?.close()
            } catch (closeException: IOException) {
                return null
            }
            return null
        }
        return socket
    }

}