package com.wyq.base.okgo

/**
 * Created by wyq
 */
data class ResultBean<T> (var errMsg: String, var resultCode: Int, var data: T)