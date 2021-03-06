package com.ts.base.printer.jqPrinter.cpcl;

import com.ts.base.printer.jqPrinter.PrinterParam;

import java.io.IOException;

public class Image extends BaseCPCL {

    public Image(PrinterParam param) {
        super(param);
        // TODO Auto-generated constructor stub
    }


    /*图像打印函数
    width：位图的字节宽度
    height： 位图的点高度
    x：位图开始的x坐标
    y；位图开始的y坐标
    data：位图数据*/
    public boolean Image_drawout(int width, int height, int x, int y, String data) throws IOException {
        String CPCLCmd = "EG " + width + " " + height + " " + x + " " + y + " " + data;
        return portSendCmd(CPCLCmd);
    }


}
