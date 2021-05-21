package com.ts.base.mvp

/**
 *
 * Created by ts
 * Date: 2019/1/16
 */
interface IPresenter<T> {
    fun attachView(view : T)
    fun detachView()
}