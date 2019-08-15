package com.wyq.base.mvp

/**
 *
 * Created by wyq
 * Date: 2019/1/16
 */
abstract class BasePresenter<T> : IPresenter<T> {
    private var view: T? = null

    override fun attachView(view: T) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    public fun getView(): T? {
        return this.view
    }

}