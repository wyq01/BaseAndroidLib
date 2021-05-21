package com.ts.base.util

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore

/**
 * 系统多媒体调用
 */
object MedialIntentUtil {
    const val REQUEST_AUDIO = 10001
    const val REQUEST_VIDEO = 10002

    fun startAudioRecord(activity: Activity) {
        startAudioRecord(
            activity,
            REQUEST_AUDIO
        )
    }

    fun startAudioRecord(activity: Activity, requestCode: Int) {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 需要权限
     */
    fun startVideoRecord(activity: Activity) {
        startVideoRecord(
            activity,
            REQUEST_VIDEO
        )
    }

    /**
     * 需要权限
     */
    fun startVideoRecord(activity: Activity, requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//        val fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO)
//        captureImageCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // 指定要保存的位置。
        //captureImageCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, ); // 设置拍摄的质量
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10) // 限制持续时长
        activity.startActivityForResult(intent, requestCode)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            data?.let {
//                when (requestCode) {
//                    MedialIntentUtil.REQUEST_VIDEO -> {
//                        val uri = it.data
//                        val path = UriUtils.getPath(this, uri)
//                        ToastUtil.shortToast(this, path)
//                        LogUtil.d(path)
//                    }
//                    MedialIntentUtil.REQUEST_AUDIO -> {
//                        val uri = it.data
//                        val path = UriUtils.getPath(this, uri)
//                        ToastUtil.shortToast(this, path)
//                        LogUtil.d(path)
//                    }
//                }
//            }
//        }
//    }

}