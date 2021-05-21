package com.ts.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.blankj.utilcode.util.KeyboardUtils
import com.ts.base.event.BaseEvent
import com.ts.base.view.LoadingLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 懒加载fragment
 */
abstract class BaseAbstractFragment : Fragment() {

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