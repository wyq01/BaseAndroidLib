package com.wyq.base.util

import android.content.Context

/**
 * 极光推送设置别名
 * Created by wyq on 2017/5/23.
 */
object AliasUtil {

    fun isAliasRegistered(context: Context): Boolean {
        return SPUtil.get(context, "alias", false) as Boolean
    }

    /**
     * 设置别名
     */
    fun registerAlias(context: Context) {
        //        UserInfo userInfo = UserUtil.getUserInfo(context);
        //        if (userInfo == null) {
        //            return;
        //        }
        //        String alias = MD5Util.encodeMD5(String.valueOf(userInfo.getId()));
        //        LogUtil.d("别名: " + alias);
        //        JPushInterface.setAlias(context, alias, new TagAliasCallback() {
        //                    @Override
        //                    public void gotResult(int code, String alias, Set<String> tags) {
        //                        switch (code) {
        //                            case 0:
        //                                SPUtil.put(context, "alias", true);
        //                                LogUtil.d("别名注册成功");
        //                                break;
        //                            case 6002: // 设置失败
        //                                SPUtil.put(context, "alias", false);
        //                                // 延迟 60 秒来调用 Handler 设置别名
        //                                new Handler().postDelayed(new Runnable() {
        //                                    @Override
        //                                    public void run() {
        //                                        registerAlias(context);
        //                                    }
        //                                }, 60 * 1000);
        //                                break;
        //                        }
        //                    }
        //                });
    }

}