package com.ts.base.http

/**
 * 返回结果
 * 需要自行拷贝实现
 */
data class IResultBean<T>(var msg: String, var errcode: Int, var data: T)
