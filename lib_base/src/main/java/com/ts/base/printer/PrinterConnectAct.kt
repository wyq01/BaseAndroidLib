package com.ts.base.printer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.base.BaseApplication
import com.ts.base.BuildConfig
import com.ts.base.R
import com.ts.base.activity.BaseActivity
import com.ts.base.constant.RequestCode
import com.ts.base.printer.event.BluetoothBindEvent
import com.ts.base.printer.event.BluetoothStatusEvent
import com.ts.base.printer.event.PrinterConnectEvent
import com.ts.base.printer.jqPrinter.PrinterConnectService
import com.ts.base.util.ToastUtil
import com.ts.base.util.click
import com.ts.base.view.ItemDivider
import com.ts.base.view.ProgressDialog
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.base_act_printer_connect.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 打印机配置
 * Created by ts
 */
class PrinterConnectAct : BaseActivity() {

    companion object {
        @JvmStatic
        fun continuePrint(data: Intent?): Boolean {
            data?.let {
                return it.getBooleanExtra("continuePrint", false)
            } ?: let {
                return false
            }
        }

        @JvmStatic
        fun startActivity(context: Activity) {
            startActivity(context, false)
        }
        @JvmStatic
        fun startActivity(context: Activity, continuePrint: Boolean) {
            val intent = Intent(context, PrinterConnectAct::class.java)
            intent.putExtra("continuePrint", continuePrint)
            context.startActivityForResult(intent, RequestCode.REQUEST_PRINTER_CONNECT)
        }
    }

    private val sourceData = ArrayList<BluetoothDevice>()
    private var printerAdapter: PrinterAdapter? = null

    private var continuePrint = false

    private val isConnect: Boolean
        get() {
            val printer = (application as BaseApplication).getJQPrinter()
            return printer?.isOpen ?: false
        }

    override fun initData(intent: Intent) {
        super.initData(intent)

        continuePrint = intent.getBooleanExtra("continuePrint", false)
    }

    override fun initViews() {
        super.initViews()

        titleTv?.text = "连接打印机"
        if (BuildConfig.DEBUG) {
            rightBtn?.visibility = View.VISIBLE
        }
        val up = ContextCompat.getDrawable(this, R.drawable.base_ic_print)
        val drawableUp = DrawableCompat.wrap(up!!)
        DrawableCompat.setTint(drawableUp, ContextCompat.getColor(this, android.R.color.white))
        rightBtn?.setImageDrawable(drawableUp)

        val manager = LinearLayoutManager(this)
        manager.orientation = LinearLayoutManager.VERTICAL
        devicesRv.layoutManager = manager
        devicesRv.addItemDecoration(
            ItemDivider(
                this,
                1,
                resources.getColor(R.color.base_divider)
            )
        )
        printerAdapter = PrinterAdapter(this, sourceData)
        devicesRv.adapter = printerAdapter
        printerAdapter?.setOnItemClickListener { baseQuickAdapter, _, position ->
            val device = baseQuickAdapter.getItem(position) as BluetoothDevice?
            connect(device?.address)
        }
        val deviceAddress = (application as BaseApplication).getDeviceAddress()
        if (isConnect) {
            printerAdapter?.updateSelected(deviceAddress)
        }

        pairBtn.click {
            startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
        }
        rightBtn?.click {
            PrintConfig.testPrint(this)
        }
    }

    override fun getData() {
        super.getData()

        if (BluetoothUtil.isBluetoothOn) {
            getPairedDevices()
        } else {
            BluetoothUtil.openBluetooth(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.REQUEST_BLUETOOTH -> getPairedDevices()
            }
        } else {
            printerAdapter?.showEmpty()
        }
    }

    private fun getPairedDevices() {
        printerAdapter?.showLoading()
        Observable.create(ObservableOnSubscribe<List<BluetoothDevice>> { emitter ->
                emitter.onNext(BluetoothUtil.pairedPrinterDevices)
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<BluetoothDevice>> {
                override fun onSubscribe(d: Disposable) {
                    printerAdapter?.showEmpty()
                }
                override fun onNext(bluetoothDevices: List<BluetoothDevice>) {
                    if (bluetoothDevices.isEmpty()) {
                        printerAdapter?.showEmpty()
                    } else {
                        sourceData.clear()
                        sourceData.addAll(bluetoothDevices)
                        printerAdapter?.notifyDataSetChanged()
                    }
                }
                override fun onError(e: Throwable) {
                    printerAdapter?.showEmpty()
                }
                override fun onComplete() {}
            })
    }

    override fun initLayout(): Int {
        return R.layout.base_act_printer_connect
    }

    override fun overStatusBar(): Boolean {
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBluetoothStatusEvent(event: BluetoothStatusEvent) {
        when (event.status) {
            BluetoothAdapter.STATE_OFF -> {
                printerAdapter?.showEmpty()
                sourceData.clear()
                printerAdapter?.notifyDataSetChanged()
            }
            BluetoothAdapter.STATE_ON -> getPairedDevices()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBluetoothDeviceEvent(event: BluetoothBindEvent) {
        when (event.status) {
            BluetoothDevice.BOND_BONDED, BluetoothDevice.BOND_NONE -> getPairedDevices()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPrinterConnectEvent(event: PrinterConnectEvent) {
        when (event.status) {
            PrinterConnectEvent.STATUS_CONNECTING -> ProgressDialog.showDialog(
                this,
                "正在连接打印机",
                true,
                DialogInterface.OnDismissListener {
                    connect("")
                })
            PrinterConnectEvent.STATUS_FAILED -> {
                ToastUtil.shortToast(this, "打印机连接失败")
                ProgressDialog.hideDialog()
                printerAdapter?.updateSelected("")
            }
            PrinterConnectEvent.STATUS_SUCCESS -> {
                ToastUtil.shortToast(this, "打印机连接成功")
                ProgressDialog.hideDialog()
                val deviceAddress = (application as BaseApplication).getDeviceAddress()
                printerAdapter?.updateSelected(deviceAddress)
                val data = Intent()
                data.putExtra("continuePrint", continuePrint)
                setResult(RESULT_OK, data)
                finish()
            }
            PrinterConnectEvent.STATUS_CONNECTED -> {
                ToastUtil.shortToast(this, "打印机已连接")
                ProgressDialog.hideDialog()
            }
        }
    }

    private fun connect(address: String?) {
        startService(address)
    }

    private fun startService(address: String?) {
        val intent = Intent(this@PrinterConnectAct, PrinterConnectService::class.java)
        intent.putExtra(BluetoothUtil.BLUETOOTH_ADDRESS, address)
        startService(intent)
    }

}