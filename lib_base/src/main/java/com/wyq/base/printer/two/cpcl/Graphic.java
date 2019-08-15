package com.wyq.base.printer.two.cpcl;

import com.wyq.base.printer.two.PrinterParam;

import java.io.IOException;

public class Graphic extends BaseCPCL {
    public Graphic(PrinterParam param) {
        super(param);
    }

    public boolean line(int startX, int startY, int endX, int endY, int width) throws IOException {
        if (startX > 575)
            startX = 575;
        if (endX > 575)
            endX = 575;

        String CPCLCmd = "LINE " + startX + " " + startY + " " + endX + " " + endY + " " + width;
        return portSendCmd(CPCLCmd);
    }

    public boolean rectangle(int startX, int startY, int endX, int endY, int width) throws IOException {
        if (startX > 575)
            startX = 575;
        if (endX > 575)
            endX = 575;

        String CPCLCmd = "BOX " + startX + " " + startY + " " + endX + " " + endY + " " + width;
        return portSendCmd(CPCLCmd);
    }

    public boolean inverse_line(int startX, int startY, int endX, int endY, int width) throws IOException {
        if (startX > 575)
            startX = 575;
        if (endX > 575)
            endX = 575;

        String CPCLCmd = "IL " + startX + " " + startY + " " + endX + " " + endY + " " + width;
        return portSendCmd(CPCLCmd);
    }
}
