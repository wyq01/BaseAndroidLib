package com.ts.base.activity

import android.content.Intent
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class IBaseActivity : AppCompatActivity() {

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