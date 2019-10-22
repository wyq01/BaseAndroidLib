package com.wyq.base

import android.content.Intent
import android.os.Handler
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity

abstract class BaseAbstractActivity : AppCompatActivity() {

    /**
     * 布局
     */
    @LayoutRes
    protected abstract fun initLayout(): Int

    /**
     * 初始化数据
     */
    protected open fun initData(intent: Intent) {}

    /**
     * 初始化ui
     */
    protected open fun initViews() {}

    /**
     * 懒初始化ui
     */
    protected open fun lazyInitViews() {}

    /**
     * 获取数据
     */
    protected open fun getData() {}


}