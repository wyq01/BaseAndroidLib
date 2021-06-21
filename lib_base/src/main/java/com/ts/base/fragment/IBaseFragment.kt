package com.ts.base.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * 懒加载fragment
 */
abstract class IBaseFragment : Fragment() {

    protected open fun onVisible() {}

    protected open fun onInvisible() {}

    /** 布局 */
    @LayoutRes
    protected abstract fun initLayout(): Int

    /** 初始化数据 */
    protected open fun initData(arguments: Bundle?) {}

    /** 初始化ui */
    protected open fun initViews(view: View) {}

    /** 获取数据 */
    protected open fun getData() {}

}