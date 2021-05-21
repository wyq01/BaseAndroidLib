package com.ts.base.printer.jqPrinter;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.ts.base.BaseApplication;
import com.ts.base.BaseApplication;
import com.ts.base.printer.BluetoothUtil;
import com.ts.base.printer.event.BluetoothStatusEvent;
import com.ts.base.printer.event.PrinterConnectEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 第二种打印机打印方式
 */
public class PrinterConnectService extends Service {
    private JQPrinter printer;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        printer = ((BaseApplication) getApplication()).getJQPrinter();
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String deviceAddress = intent.getStringExtra(BluetoothUtil.BLUETOOTH_ADDRESS);
        if (!TextUtils.isEmpty(deviceAddress)) {
            autoConnect(deviceAddress);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void autoConnect(String deviceAddress) {
        connect(deviceAddress);
    }

    private void connect(final String deviceAddress) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                printer.close();
                emitter.onNext(printer.open(deviceAddress));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        EventBus.getDefault().post(new PrinterConnectEvent(PrinterConnectEvent.STATUS_CONNECTING));
                    }
                    @Override
                    public void onNext(Boolean result) {
                        if (result) {
                            LogUtils.d("打印机连接成功");
                            ((BaseApplication) getApplication()).setDeviceAddress(deviceAddress);
                            EventBus.getDefault().post(new PrinterConnectEvent(PrinterConnectEvent.STATUS_SUCCESS));
                        } else {
                            EventBus.getDefault().post(new PrinterConnectEvent(PrinterConnectEvent.STATUS_FAILED));
                            LogUtils.d("打印机连接失败");
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new PrinterConnectEvent(PrinterConnectEvent.STATUS_FAILED));
                        LogUtils.d("打印机连接失败");
                    }
                    @Override
                    public void onComplete() {}
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothStatusEvent(BluetoothStatusEvent event) {
        switch (event.getStatus()) {
            case BluetoothAdapter.STATE_ON: // 蓝牙打开后重新连接
                autoConnect(((BaseApplication) getApplication()).getDeviceAddress());
                break;
        }
    }

}