package com.wyq.base.printer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.wyq.base.BaseActivity
import com.wyq.base.BaseApplication
import com.wyq.base.BuildConfig
import com.wyq.base.R
import com.wyq.base.constant.RequestCode
import com.wyq.base.printer.event.BluetoothBindEvent
import com.wyq.base.printer.event.BluetoothStatusEvent
import com.wyq.base.printer.event.PrinterConnectEvent
import com.wyq.base.printer.jqPrinter.PrinterConnectService
import com.wyq.base.util.ToastUtil
import com.wyq.base.util.click
import com.wyq.base.view.ItemDivider
import com.wyq.base.view.ProgressDialog
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
 * Created by wyq
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
        printerAdapter?.onItemClickListener = BaseQuickAdapter.OnItemClickListener { baseQuickAdapter, _, position ->
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