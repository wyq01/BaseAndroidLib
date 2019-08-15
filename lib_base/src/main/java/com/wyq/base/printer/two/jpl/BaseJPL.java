package com.wyq.base.printer.two.jpl;

import com.wyq.base.printer.two.PrinterParam;
import com.wyq.base.printer.two.port.Port;

public class BaseJPL {
    protected PrinterParam _param;
    protected Port _port;
    protected byte _cmd[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    /*
     * 构造函数
     */
    public BaseJPL(PrinterParam param) {
        _param = param;
        _port = _param.port;
    }
}
