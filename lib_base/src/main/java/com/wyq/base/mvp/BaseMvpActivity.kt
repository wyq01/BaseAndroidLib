package com.wyq.base.mvp

import android.os.Bundle
import com.wyq.base.BaseActivity

/**
 *
 * Created by wyq
 * Date: 2019/1/16
 */
abstract class BaseMvpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        attach()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        detach()
    }

    protected abstract fun inject()
    protected abstract fun attach()
    protected abstract fun detach()
}