package com.ts.base.printer

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ts.base.adapter.BaseAda
import com.ts.base.R
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by ts
 */
class PrinterAdapter(context: Context, sourceData: List<BluetoothDevice>?) :
    BaseAda<BluetoothDevice, PrinterAdapter.ViewHolder>(context, R.layout.base_ada_printer, sourceData) {

    private var deviceAddress: String? = null

    fun updateSelected(deviceAddress: String?) {
        this.deviceAddress = deviceAddress
        notifyDataSetChanged()
    }

    override fun convert(viewHolder: ViewHolder, bluetoothDevice: BluetoothDevice) {
        viewHolder.deviceNameTv?.text = bluetoothDevice.name ?: bluetoothDevice.address
        viewHolder.checkIv?.visibility = if (bluetoothDevice.address == deviceAddress) View.VISIBLE else View.INVISIBLE
    }

    inner class ViewHolder(view: View) : BaseViewHolder(view) {
        internal var deviceNameTv = view.findViewById<TextView>(R.id.deviceNameTv)
        internal var checkIv = view.findViewById<ImageView>(R.id.checkIv)
    }

}