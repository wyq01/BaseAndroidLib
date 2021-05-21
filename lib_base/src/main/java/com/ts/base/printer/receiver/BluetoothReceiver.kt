package com.ts.base.printer.receiver

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.ts.base.printer.event.BluetoothBindEvent
import com.ts.base.printer.event.BluetoothStatusEvent

import org.greenrobot.eventbus.EventBus

/**
 * 接收蓝牙状态改变广播
 * Created by ts on 2017/10/13.
 */
class BluetoothReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> EventBus.getDefault().post(
                        BluetoothStatusEvent(
                            BluetoothAdapter.STATE_OFF
                        )
                    )
                    BluetoothAdapter.STATE_ON -> EventBus.getDefault().post(
                        BluetoothStatusEvent(
                            BluetoothAdapter.STATE_ON
                        )
                    )
                    BluetoothAdapter.STATE_DISCONNECTED -> EventBus.getDefault().post(
                        BluetoothStatusEvent(
                            BluetoothAdapter.STATE_DISCONNECTED
                        )
                    )
                }
            }
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                var address: String? = null
                if (device != null) {
                    address = device.address
                }
                when (state) {
                    BluetoothDevice.BOND_NONE -> EventBus.getDefault().post(
                        BluetoothBindEvent(
                            BluetoothDevice.BOND_NONE,
                            address
                        )
                    )
                    BluetoothDevice.BOND_BONDING -> EventBus.getDefault().post(
                        BluetoothBindEvent(
                            BluetoothDevice.BOND_BONDING,
                            address
                        )
                    )
                    BluetoothDevice.BOND_BONDED -> EventBus.getDefault().post(
                        BluetoothBindEvent(
                            BluetoothDevice.BOND_BONDED,
                            address
                        )
                    )
                }
            }
        }
    }

}