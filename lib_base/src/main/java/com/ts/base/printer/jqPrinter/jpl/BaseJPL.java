package com.ts.base.printer.jqPrinter.jpl;

import com.ts.base.printer.jqPrinter.port.Port;
import com.ts.base.printer.jqPrinter.PrinterParam;
import com.ts.base.printer.jqPrinter.port.Port;

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
