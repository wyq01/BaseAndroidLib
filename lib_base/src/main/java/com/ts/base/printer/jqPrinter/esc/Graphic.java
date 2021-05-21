package com.ts.base.printer.jqPrinter.esc;

import com.ts.base.printer.jqPrinter.PrinterParam;

import java.io.IOException;

public class Graphic extends BaseESC {
    /*
     * 构造函数
     */
    public Graphic(PrinterParam param) {
        super(param);
    }

    /*
     * 画线段
     */
    public boolean linedrawOut(ESC.LINE_POINT[] line_points) throws IOException {
        byte line_count = (byte) line_points.length;
        if (line_count > 8)
            return false;
        _cmd[0] = 0x1D;
        _cmd[1] = 0x27;
        _cmd[2] = line_count;
        if (!_port.write(_cmd, 0, 3))
            return false;
        byte[] data = {0, 0, 0, 0};
        for (int i = 0; i < line_count; i++) {
            if (line_points[i].startPoint < 0)
                line_points[i].startPoint = 0;
            if (line_points[i].startPoint >= _param.escPageWidth)
                line_points[i].startPoint = _param.escPageWidth - 1;
            data[0] = (byte) line_points[i].startPoint;
            data[1] = (byte) (line_points[i].startPoint >> 8);
            data[2] = (byte) line_points[i].endPoint;
            data[3] = (byte) (line_points[i].endPoint >> 8);
            if (!_port.write(data, 0, 4))
                return false;
        }
        return true;
    }

    /************************** 新版打印分割线 *************************/
    /**
     * @param linePoints 线上点数
     * @throws IOException
     */
    public void drawLine(ESC.LINE_POINT[] linePoints) throws IOException {
        byte line_count = (byte) linePoints.length;
        if (line_count > 8)
            return;
        _cmd[0] = 0x1D;
        _cmd[1] = 0x27;
        _cmd[2] = line_count;
        _port.write(_cmd, 0, 3);
        byte[] data = {0, 0, 0, 0};
        for (int i = 0; i < line_count; i++) {
            if (linePoints[i].startPoint < 0)
                linePoints[i].startPoint = 0;
            if (linePoints[i].startPoint >= _param.escPageWidth)
                linePoints[i].startPoint = _param.escPageWidth - 1;
            data[0] = (byte) linePoints[i].startPoint;
            data[1] = (byte) (linePoints[i].startPoint >> 8);
            data[2] = (byte) linePoints[i].endPoint;
            data[3] = (byte) (linePoints[i].endPoint >> 8);
            _port.write(data, 0, 4);
        }
    }

}
