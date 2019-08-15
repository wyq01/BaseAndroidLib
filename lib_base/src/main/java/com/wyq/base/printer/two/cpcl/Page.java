package com.wyq.base.printer.two.cpcl;

import com.wyq.base.printer.two.PrinterParam;

import java.io.IOException;

public class Page extends BaseCPCL {
    public Page(PrinterParam param) {
        super(param);
    }

    /*
     * 页面开始
     * pageHeight:打印标签的最大高度点数（1点=0.125mm）。
     * printCount: 打印标签数量. 最大为 1024
     */
    public boolean start(int pageHeight, int Pagewidth, int printCount) throws IOException {
        String cmd = "! 0 200 200 " + pageHeight + " " + printCount;
        portSendCmd(cmd);
        cmd = "PAGE-WIDTH " + Pagewidth;
        portSendCmd(cmd);
        return true;
    }

    /*
     * 设置页面宽度
     * width:页面宽度点数
     */
    public boolean setPageWidth(int width) throws IOException {
        String cmd = "PAGE-WIDTH " + width;
        return portSendCmd(cmd);
    }

    /*
     * 打印页面
     */
    public boolean print() throws IOException {
        String cmd = "PRINT";
        return portSendCmd(cmd);
    }

    /*
     * 走纸到定位点
     * 必须和<BAR-SENSE>/<GAP-SENSE>指令配合
     * 控制内容的注释，不会被打印出来
     * 只能在<!> Commands和PRINT指令之间才有效。
     */
    public boolean feed() throws IOException {
        String cmd = "FORM";
        return portSendCmd(cmd);
    }

    /*
     * 注释
     */
    public boolean notes(String text) throws IOException {
        String cmd = ";" + text;
        return portSendCmd(cmd);
    }

    public boolean bar_sense_right() throws IOException {
        String cmd = "BAR-SENSE";
        return portSendCmd(cmd);
    }

    public boolean bar_sense_left() throws IOException {
        String cmd = "BAR-SENSE LEFT";
        return portSendCmd(cmd);
    }

    public boolean gap_sense() throws IOException {
        String cmd = "GAP-SENSE";
        return portSendCmd(cmd);
    }

    public boolean left() throws IOException {
        String cmd = "LEFT";
        return portSendCmd(cmd);
    }

    public boolean center() throws IOException {
        String cmd = "CENTER";
        return portSendCmd(cmd);
    }

    public boolean right() throws IOException {
        String cmd = "RIGHT";
        return portSendCmd(cmd);
    }

    public boolean end() throws IOException {
        String cmd = "END";
        return portSendCmd(cmd);
    }

    public boolean abort() throws IOException {
        String cmd = "ABORT";
        return portSendCmd(cmd);
    }

    public boolean contrast(int level) throws IOException {
        String cmd = "CONTRAST " + level;
        return portSendCmd(cmd);
    }

    public boolean speed(int speed_level) throws IOException {
        String cmd = "CONTRAST " + speed_level;
        return portSendCmd(cmd);
    }

    public boolean prefeed(int length) throws IOException {
        String cmd = "PREFEED " + length;
        return portSendCmd(cmd);
    }

    public boolean postfeed(int length) throws IOException {
        String cmd = "POSTFEED " + length;
        return portSendCmd(cmd);
    }
}
