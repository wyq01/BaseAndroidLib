package com.ts.base.okgo

/**
 * Created by ts
 */
data class ResultBean<T> (var errMsg: String, var resultCode: Int, var data: T)