package com.ts.base.js;

import android.app.Activity;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;

import cn.pedant.SafeWebViewBridge.JsCallback;

/**
 * js交互方法，需要被JS调用的函数，必须定义成public static，且必须包含WebView这个参数
 */
public class JsScope {

//    /**
//     * 调用扫码，并返回
//     */
//    public static void getQRCode(WebView webView, JsCallback jsCallback) {
//        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
//        activity.getQrCode(jsCallback);
//    }
//    /**
//     * 调用相机和相册
//     */
//    public static void getAlbum(WebView webView, JsCallback jsCallback) {
//        BaseWebAct activity = (BaseWebAct) webView.getContext();
//        activity.getAlbum(jsCallback);
//    }
//    /**
//     * 调用相机和相册
//     */
//    public static void audioRecord(WebView webView, JsCallback jsCallback) {
//        BaseWebAct activity = (BaseWebAct) webView.getContext();
//        activity.audioRecord(jsCallback);
//    }
//    /**
//     * 调用相机和相册
//     */
//    public static void videoPlay(WebView webView, String coverUrl, String videoUrl) {
//        BaseWebAct activity = (BaseWebAct) webView.getContext();
//        Intent intent = new Intent(activity, VideoActivity.class);
//        intent.putExtra("cover", coverUrl);
//        intent.putExtra("video", videoUrl);
//        activity.startActivity(intent);
//    }
//    /**
//     * 显示对话框
//     */
//    public static void showDialog(WebView webView, String msg) {
//        new BaseDialog.Builder(webView.getContext())
//                .setMessage(msg)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .create()
//                .show();
//    }
//    /**
//     * 弹出toast
//     * @param webView    浏览器
//     * @param message    提示信息
//     */
//    public static void toast(WebView webView, String message) {
//        ToastUtils.showShort(message);
//    }

    /**
     * 结束当前窗口
     */
    public static void closeWebView(WebView webView) {
        LogUtils.d("closeWebView");
        if (webView.getContext() instanceof Activity) {
            ((Activity) webView.getContext()).finish();
        }
    }

    /**
     * 切换屏幕方向
     */
    public static void toggleScreenOrientation(WebView webView) {
        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
        activity.toggleScreenOrientation();
    }

    /**
     * 屏幕是否为横屏
     */
    public static void screenIsLandscape(WebView webView, JsCallback jsCallback) {
        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
        activity.screenIsLandscape(jsCallback);
    }

    /**
     * 是否开启重力感应
     *
     * @param sensorRotate true开启，false关闭
     */
    public static void enableSensorRotate(WebView webView, boolean sensorRotate) {
        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
        activity.enableSensorRotate(sensorRotate);
    }

    /**
     * 调起手写签名
     */
    public static void sign(WebView webView, JsCallback jsCallback) {
        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
        activity.sign(jsCallback);
    }

    /**
     * 打印
     */
    public static void print(WebView webView, String json, JsCallback jsCallback) {
        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
        activity.print(json, jsCallback);
    }

    public static void album(WebView webView, JsCallback jsCallback) {
        album(webView, true, jsCallback);
    }

    public static void album(WebView webView, boolean needCapture, JsCallback jsCallback) {
        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
        activity.album(needCapture, jsCallback);
    }

    public static void singleUpload(WebView webView, String path, String url, long timeout, JsCallback jsCallback) {
        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
        activity.singleUpload(path, url, timeout, jsCallback);
    }

    public static void cancelUpload(WebView webView) {
        BaseWebActivity activity = (BaseWebActivity) webView.getContext();
        activity.cancelUpload();
    }

}