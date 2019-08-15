package com.wyq.base.printer.two.cpcl;

import com.wyq.base.printer.two.PrinterParam;

import java.io.IOException;

public class CPCL {
    private byte[] _cmd = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private PrinterParam _param;
    public Page page;
    public Text text;
    public Graphic graphic;
    public Barcode barcode;
    public Image image;

    public CPCL(PrinterParam param) {
        _param = param;

        page = new Page(_param);
        text = new Text(_param);
        graphic = new Graphic(_param);
        barcode = new Barcode(_param);
        image = new Image(_param);
    }

    public boolean feedMarkBegin() throws IOException {
        _cmd[0] = 0x0E;
        return _param.port.write(_cmd, 0, 1);
    }


}
