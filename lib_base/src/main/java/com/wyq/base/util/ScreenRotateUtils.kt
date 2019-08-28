package com.wyq.base.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.blankj.utilcode.util.LogUtils

/**
 * 重力感应处理工具类
 */
class ScreenRotateUtils(context: Context) {

    private var mActivity: Activity? = null

//    private var ignoreSystemSensorRotate = true // 是否忽略系统设置
    private var openSensorRotate = false // 是否打开重力感应

    private var sm: SensorManager? = null
    private var listener: OrientationSensorListener? = null
    private var sensor: Sensor? = null
    private var mHandler: Handler? = null

    companion object {
        private const val DATA_X = 0
        private const val DATA_Y = 1
        private const val DATA_Z = 2
        const val ORIENTATION_UNKNOWN = -1
        @SuppressLint("StaticFieldLeak")
        private var instance: ScreenRotateUtils? = null

        /**
         * 初始化，获取实例
         * @param context
         * @return
         */
        fun getInstance(context: Context): ScreenRotateUtils {
            LogUtils.d("getInstance")
            if (instance == null) {
                instance = ScreenRotateUtils(context)
            }
            return instance!!
//            return ScreenRotateUtils(context)
        }

        /**
         * 切换屏幕方向
         */
        fun changeScreenOrientation(activity: Activity) {
            activity.requestedOrientation = when(activity.requestedOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        /**
         * 屏幕是否为横屏
         */
        fun screenIsLandscape(activity: Activity): Boolean {
            return (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    || (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
        }
    }

    /**
     * 重力感应监听者
     */
    inner class OrientationSensorListener(private val rotateHandler: Handler?) : SensorEventListener {

        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
//            LogUtil.d("onSensorChanged")
            val values = event.values
            var orientation = ORIENTATION_UNKNOWN
            val x = -values[DATA_X]
            val y = -values[DATA_Y]
            val z = -values[DATA_Z]
            val magnitude = x * x + y * y
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= z * z) {
                // 屏幕旋转时
                val oneEightyOverPi = 57.29577957855f
                val angle = Math.atan2((-y).toDouble(), x.toDouble()).toFloat() * oneEightyOverPi
                orientation = 90 - Math.round(angle)
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360
                }
                while (orientation < 0) {
                    orientation += 360
                }
            }
            /**
             * 获取手机系统的重力感应开关设置，这段代码看需求，不要就删除
             * screenchange = 1 表示开启，screenchange = 0 表示禁用
             * 要是禁用了就直接返回
             */
//            if (!ignoreSystemSensorRotate) {
//                try {
//                    val isRotate = Settings.System.getInt(mActivity!!.contentResolver, Settings.System.ACCELEROMETER_ROTATION)
//                    // 如果用户禁用掉了重力感应就直接return
//                    if (isRotate == 0) return
//                } catch (e: Settings.SettingNotFoundException) {
//                    e.printStackTrace()
//                }
//            }

            // 判断是否要进行中断信息传递
            if (openSensorRotate) {
//                LogUtil.d("切换横竖屏")
                rotateHandler?.obtainMessage(888, orientation, 0)?.sendToTarget()
            }
        }
    }

    /**
     * 开启监听
     * 绑定切换横竖屏Activity的生命周期，在onResume方法中执行
     * @param activity
     */
    fun registerSensorRotate(activity: Activity) {
        unregisterSensorRotate()
        mActivity = activity
        sm?.let {
            val result = it.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    /**
     * 注销监听
     * 解除绑定切换横竖屏Activity的生命周期，在onPause方法中执行
     */
    fun unregisterSensorRotate() {
        sm?.unregisterListener(listener)
        mActivity = null  // 防止内存泄漏
    }

//    fun ignoreSystemSensorRotate(ignoreSystemSensorRotate: Boolean) {
//        this.ignoreSystemSensorRotate = ignoreSystemSensorRotate
//    }

    fun enableSensorRotate(openSensorRotate: Boolean) {
        this.openSensorRotate = openSensorRotate
    }

    fun toggleSensorRotate() {
        enableSensorRotate(!openSensorRotate)
    }

    fun isSensorRotateOpen(): Boolean {
        return this.openSensorRotate
    }

    init {
        LogUtils.d("init")
        sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        sensor = sm!!.getDefaultSensor(Sensor.TYPE_GRAVITY)
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what == 888) {
                    val orientation = msg.arg1
                    /**
                     * 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
                     */
                    if (orientation in 45..135) {
                        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        LogUtils.d("8 反向横屏 ${mActivity!!.requestedOrientation}")
                    } else if (orientation in 136..225) {
                        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        LogUtils.d("9 反向竖屏 ${mActivity!!.requestedOrientation}")
                    } else if (orientation in 226..315) {
                        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        LogUtils.d("0 正向横屏 ${mActivity!!.requestedOrientation}")
                    } else if (orientation in 316..360 || orientation in 0..44) {
                        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        LogUtils.d("1 正向竖屏 ${mActivity!!.requestedOrientation}")
                    }
                }
            }
        }
        listener = OrientationSensorListener(mHandler)
    }

}