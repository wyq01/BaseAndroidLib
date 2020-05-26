package com.wyq.base.printer.jqPrinter.cpcl;

import android.util.Log;
import com.wyq.base.printer.jqPrinter.PrinterParam;
import com.wyq.base.printer.jqPrinter.Printer_define;
import com.wyq.base.printer.jqPrinter.port.Port;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BaseCPCL {
    protected Printer_define.PRINTER_MODEL _model;
    protected Port _port;
    protected boolean _support;
    protected PrinterParam _param;
    protected byte[] _cmd = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    public BaseCPCL(PrinterParam param) {
        _param = param;
        _model = _param.model;
        _port = _param.port;
        _support = _param.cpclSupport;
    }

    protected boolean portSendCmd(String cmd) throws IOException {
        cmd += "\r\n";
        byte[] data = null;
        try {
            data = cmd.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            Log.e("JQ", "Sting getBytes('GBK') failed");
            return false;
        }
        return _port.write(data, 0, data.length);
    }
}