package com.ts.base.mvp

import android.os.Bundle
import com.ts.base.BaseFragment

/**
 *
 * Created by ts
 * Date: 2019/1/16
 */
abstract class BaseMvpFragment: BaseFragment() {

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