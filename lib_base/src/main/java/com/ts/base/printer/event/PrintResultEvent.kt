package com.ts.base.printer.event

/**
 * @param success 打印结果
 * @param content 打印内容
 * @param className 调起打印的类
 * Created by ts
 * Date: 2018/12/24
 */
class PrintResultEvent(var success: Boolean, var content: String?, var className: String)