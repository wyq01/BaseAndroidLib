package com.ts.base.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by ts
 * Date: 2018/11/6
 */
object BitmapUtil {
    fun compress(srcPath: String): Bitmap? {
        val newOpts = BitmapFactory.Options()
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true
        var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)//此时返回bm为空

        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        val hh = 800f//这里设置高度为800f
        val ww = 480f//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / hh).toInt()
        }
        if (be <= 0)
            be = 1
        newOpts.inSampleSize = be//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
        return compressImage(bitmap)//压缩好比例大小后再进行质量压缩
    }

    fun compress(image: Bitmap): Bitmap? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        if (baos.toByteArray().size / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset()//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos)//这里压缩50%，把压缩后的数据存放到baos中
        }
        var isBm = ByteArrayInputStream(baos.toByteArray())
        val newOpts = BitmapFactory.Options()
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true
        var bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        val hh = 800f//这里设置高度为800f
        val ww = 480f//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / hh).toInt()
        }
        if (be <= 0)
            be = 1
        newOpts.inSampleSize = be//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = ByteArrayInputStream(baos.toByteArray())
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
        return compressImage(bitmap!!) // 压缩好比例大小后再进行质量压缩
    }

    private fun compressImage(image: Bitmap): Bitmap? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size > FileConstant.MAX_SIZE) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset()//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10//每次都减少10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())//把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
    }
}
