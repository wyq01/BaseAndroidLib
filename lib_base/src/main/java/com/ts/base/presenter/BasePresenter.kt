package com.ts.base.presenter

import android.app.Activity
import android.app.Service
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.Utils
import java.lang.ref.WeakReference

@Suppress("UNCHECKED_CAST")
abstract class BasePresenter<V : IView>(var view: IView) : IPresenter {

    protected val viewRef: WeakReference<V> = WeakReference(view as V)

    fun getContext(): Context {
        return when(viewRef.get()) {
            is Activity -> viewRef.get() as Activity
            is Fragment -> (viewRef.get() as Fragment).activity as Activity
            is Service -> viewRef.get() as Service
            else -> Utils.getApp()
        }
    }

    init {
        (view as LifecycleOwner).lifecycle.addObserver(this@BasePresenter)
    }

    override fun onCreate(owner: LifecycleOwner) {}

    override fun onDestroy(owner: LifecycleOwner) {
        viewRef.clear()
    }

    override fun onLifecycleChanged(owner: LifecycleOwner, event: Lifecycle.Event) {}
}