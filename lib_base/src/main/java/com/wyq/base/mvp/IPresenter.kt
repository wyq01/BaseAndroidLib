package com.wyq.base.mvp

/**
 *
 * Created by wyq
 * Date: 2019/1/16
 */
interface IPresenter<T> {
    fun attachView(view : T)
    fun detachView()
}