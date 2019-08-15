package com.wyq.base.printer.one

import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

import com.github.promeg.pinyinhelper.Pinyin

import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * 蓝牙打印工具类
 */
class Printer
/**
 * 初始化Pos实例
 *
 * @param encoding 编码
 * @throws IOException
 */
@Throws(IOException::class)
constructor(outputStream: OutputStream?, encoding: String) {

    private var mWriter: OutputStreamWriter? = null
    private var mOutputStream: OutputStream? = null

    init {
        mWriter = OutputStreamWriter(outputStream, encoding)
        mOutputStream = outputStream
        initPrinter()
    }

    @Throws(IOException::class)
    fun print(bs: ByteArray) {
        mOutputStream?.write(bs)
    }

    @Throws(IOException::class)
    fun printRawBytes(bytes: ByteArray) {
        mOutputStream?.write(bytes)
        mOutputStream?.flush()
    }

    /**
     * 初始化打印机
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun initPrinter() {
        mWriter?.write(0x1B)
        mWriter?.write(0x40)
        mWriter?.flush()
    }

    /**
     * 打印换行
     *
     * @return length 需要打印的空行数
     * @throws IOException
     */
    @Throws(IOException::class)
    @JvmOverloads
    fun printLine(lineNum: Int = 1) {
        for (i in 0 until lineNum) {
            mWriter?.write("\n")
        }
        mWriter?.flush()
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    @Throws(IOException::class)
    fun printTabSpace(length: Int) {
        for (i in 0 until length) {
            mWriter?.write("\t")
        }
        mWriter?.flush()
    }

    /**
     * 绝对打印位置
     *
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun setLocation(offset: Int): ByteArray {
        val bs = ByteArray(4)
        bs[0] = 0x1B
        bs[1] = 0x24
        bs[2] = (offset % 256).toByte()
        bs[3] = (offset / 256).toByte()
        return bs
    }

    @Throws(IOException::class)
    fun getGbk(stText: String): ByteArray {
        return stText.toByteArray(charset("GBK"))
    }

    private fun getStringPixLength(str: String): Int {
        var pixLength = 0
        var c: Char
        for (i in 0 until str.length) {
            c = str[i]
            if (Pinyin.isChinese(c)) {
                pixLength += 24
            } else {
                pixLength += 12
            }
        }
        return pixLength
    }

    fun getOffset(str: String): Int {
        return WIDTH_PIXEL - getStringPixLength(str)
    }

    /**
     * 打印文字
     *
     * @param text
     * @throws IOException
     */
    @Throws(IOException::class)
    fun printText(text: String) {
        mWriter?.write(text)
        mWriter?.flush()
    }

    /**
     * 对齐0:左对齐，1：居中，2：右对齐
     */
    @Throws(IOException::class)
    fun printAlignment(alignment: Int) {
        mWriter?.write(0x1b)
        mWriter?.write(0x61)
        mWriter?.write(alignment)
    }

    @Throws(IOException::class)
    fun printLargeText(text: String) {

        mWriter?.write(0x1b)
        mWriter?.write(0x21)
        mWriter?.write(32)

        mWriter?.write(text)

        mWriter?.write(0x1b)
        mWriter?.write(0x21)
        mWriter?.write(0)

        mWriter?.flush()
    }

    @Throws(IOException::class)
    fun printTwoColumn(title: String, content: String) {
        var iNum = 0
        val byteBuffer = ByteArray(100)
        var tmp: ByteArray

        tmp = getGbk(title)
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)
        iNum += tmp.size

        tmp = setLocation(getOffset(content))
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)
        iNum += tmp.size

        tmp = getGbk(content)
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)

        print(byteBuffer)
    }

    @Throws(IOException::class)
    fun printThreeColumn(left: String, middle: String, right: String) {
        var middle = middle
        var iNum = 0
        val byteBuffer = ByteArray(200)
        var tmp = ByteArray(0)

        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)
        iNum += tmp.size

        tmp = getGbk(left)
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)
        iNum += tmp.size

        val pixLength = getStringPixLength(left) % WIDTH_PIXEL
        if (pixLength > WIDTH_PIXEL / 2 || pixLength == 0) {
            middle = "\n\t\t" + middle
        }

        tmp = setLocation(192)
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)
        iNum += tmp.size

        tmp = getGbk(middle)
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)
        iNum += tmp.size

        tmp = setLocation(getOffset(right))
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)
        iNum += tmp.size

        tmp = getGbk(right)
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.size)

        print(byteBuffer)
    }

    @Throws(IOException::class)
    fun printDashLine() {
        printText("--------------------------------")
    }

    @Throws(IOException::class)
    fun printBitmap(bmp: Bitmap) {
        val bitmap = compressPic(bmp)
        val bmpByteArray = draw2PxPoint(bitmap)
        printRawBytes(bmpByteArray)
    }

    /*************************************************************************
     * 假设一个360*360的图片，分辨率设为24, 共分15行打印 每一行,是一个 360 * 24 的点阵,y轴有24个点,存储在3个byte里面。
     * 即每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
     */
    private fun draw2PxPoint(bmp: Bitmap): ByteArray {
        //先设置一个足够大的size，最后在用数组拷贝复制到一个精确大小的byte数组中
        val size = bmp.width * bmp.height / 8 + 1000
        val tmp = ByteArray(size)
        var k = 0
        // 设置行距为0
        tmp[k++] = 0x1B
        tmp[k++] = 0x33
        tmp[k++] = 0x00
        // 居中打印
        tmp[k++] = 0x1B
        tmp[k++] = 0x61
        tmp[k++] = 1
        var j = 0
        while (j < bmp.height / 24f) {
            tmp[k++] = 0x1B
            tmp[k++] = 0x2A// 0x1B 2A 表示图片打印指令
            tmp[k++] = 33 // m=33时，选择24点密度打印
            tmp[k++] = (bmp.width % 256).toByte() // nL
            tmp[k++] = (bmp.width / 256).toByte() // nH
            for (i in 0 until bmp.width) {
                for (m in 0..2) {
                    for (n in 0..7) {
                        val b = px2Byte(i, j * 24 + m * 8 + n, bmp)
                        val temp = tmp[k]
                        tmp[k] = temp.plus(temp).plus(b).toByte()
                    }
                    k++
                }
            }
            tmp[k++] = 10// 换行
            j++
        }
        // 恢复默认行距
        tmp[k++] = 0x1B
        tmp[k++] = 0x32

        val result = ByteArray(k)
        System.arraycopy(tmp, 0, result, 0, k)
        return result
    }

    /**
     * 图片二值化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    private fun px2Byte(x: Int, y: Int, bit: Bitmap): Byte {
        if (x < bit.width && y < bit.height) {
            val b: Byte
            val pixel = bit.getPixel(x, y)
            val red = pixel and 0x00ff0000 shr 16 // 取高两位
            val green = pixel and 0x0000ff00 shr 8 // 取中两位
            val blue = pixel and 0x000000ff // 取低两位
            val gray = RGB2Gray(red, green, blue)
            if (gray < 128) {
                b = 1
            } else {
                b = 0
            }
            return b
        }
        return 0
    }

    /**
     * 图片灰度的转化
     */
    private fun RGB2Gray(r: Int, g: Int, b: Int): Int {
        return (0.29900 * r + 0.58700 * g + 0.11400 * b).toInt()
    }

    /**
     * 对图片进行压缩（去除透明度）
     *
     * @param bitmapOrg
     */
    private fun compressPic(bitmapOrg: Bitmap): Bitmap {
        // 获取这个图片的宽和高
        val width = bitmapOrg.width
        val height = bitmapOrg.height
        // 定义预转换成的图片的宽度和高度
        val newWidth = IMAGE_SIZE
        val newHeight = IMAGE_SIZE
        val targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val targetCanvas = Canvas(targetBmp)
        targetCanvas.drawColor(-0x1)
        targetCanvas.drawBitmap(bitmapOrg, Rect(0, 0, width, height), Rect(0, 0, newWidth, newHeight), null)
        return targetBmp
    }

    companion object {

        val WIDTH_PIXEL = 384
        val IMAGE_SIZE = 320

        fun printTest(bluetoothSocket: BluetoothSocket, bitmap: Bitmap) {
            try {
                val pUtil =
                    Printer(bluetoothSocket.outputStream, "GBK")
                // 店铺名 居中 放大
                pUtil.printAlignment(1)
                pUtil.printLargeText("解忧杂货店")
                pUtil.printLine()
                pUtil.printAlignment(0)
                pUtil.printLine()

                pUtil.printTwoColumn("时间:", "2017-05-09 15:50:41")
                pUtil.printLine()

                pUtil.printTwoColumn("订单号:", System.currentTimeMillis().toString() + "")
                pUtil.printLine()

                pUtil.printTwoColumn("付款人:", "VitaminChen")
                pUtil.printLine()

                // 分隔线
                pUtil.printDashLine()
                pUtil.printLine()

                //打印商品列表
                pUtil.printText("商品")
                pUtil.printTabSpace(2)
                pUtil.printText("数量")
                pUtil.printTabSpace(1)
                pUtil.printText("    单价")
                pUtil.printLine()

                pUtil.printThreeColumn("iphone6", "1", "4999.00")
                pUtil.printThreeColumn("测试一个超长名字的产品看看打印出来会怎么样哈哈哈哈哈哈", "1", "4999.00")

                pUtil.printDashLine()
                pUtil.printLine()

                pUtil.printTwoColumn("订单金额:", "9998.00")
                pUtil.printLine()

                pUtil.printTwoColumn("实收金额:", "10000.00")
                pUtil.printLine()

                pUtil.printTwoColumn("找零:", "2.00")
                pUtil.printLine()

                pUtil.printDashLine()

                pUtil.printBitmap(bitmap)

                pUtil.printLine(4)
            } catch (e: IOException) {

            }
        }
    }
}
/**
 * 打印换行(只换一行)
 *
 * @throws IOException
 */