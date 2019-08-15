package com.wyq.base.printer.two.cpcl;

import com.wyq.base.printer.two.PrinterParam;

import java.io.IOException;

public class Barcode extends BaseCPCL {
    public Barcode(PrinterParam param) {
        super(param);
    }

    public boolean code128(int x, int y, int bar_height, int unit_width, String text) throws IOException {
        int radio = 2;
        String CPCLCmd = "BARCODE 128 " + unit_width + " " + radio + " " + bar_height + " " + x + " " + y + " " + text;
        return portSendCmd(CPCLCmd);
    }

    public boolean code128v(int x, int y, int bar_height, int unit_width, String text) throws IOException {
        int radio = 2;
        String CPCLCmd = "VBARCODE 128 " + unit_width + " " + radio + " " + bar_height + " " + x + " " + y + " " + text;
        return portSendCmd(CPCLCmd);
    }

    public boolean QRCode(int x, int y, int unit_width, int version, String text) throws IOException {
        String CPCLCmd = "SETQRVER " + version;
        if (!portSendCmd(CPCLCmd)) return false;
        CPCLCmd = "BARCODE QR " + x + " " + y + " M 2 " + "U " + unit_width + "\r\nMA," + text + "\r\nENDQR";
        return portSendCmd(CPCLCmd);
    }

    public boolean Barcode_Text(int font_numder, int font_size, int offset) throws IOException {
        String CPCLCmd = "BARCODE-TEXT " + font_numder + " " + font_size + " " + offset;
        return portSendCmd(CPCLCmd);
    }
}