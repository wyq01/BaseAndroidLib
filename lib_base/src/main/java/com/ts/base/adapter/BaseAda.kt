package com.ts.base.adapter

import android.content.Context
import android.view.LayoutInflater
import com.ts.base.R
import com.ts.base.view.CommonShapeButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ts.base.util.ClickUtil
import com.ts.base.util.click

/**
 *
 * Created by ts
 * Date: 2019/1/14
 */
abstract class BaseAda<T, K : BaseViewHolder>(
    var mContext: Context,
    layoutResId: Int,
    data: MutableList<T>?
) : BaseQuickAdapter<T, K>(layoutResId, data) {
    interface OnReloadListener {
        fun onReload()
    }

    fun setOnReloadListener(l: OnReloadListener) {
        this.onReloadListener = l
    }

    fun setOnReloadListener(action: () -> Unit) {
        this.onReloadListener = object : OnReloadListener {
            override fun onReload() {
                action()
            }
        }
    }

    private var onReloadListener: OnReloadListener? = null

    fun updateItem(position: Int, item: T) {
        data[position] = item
        notifyItemChanged(headerLayoutCount + position)
    }

    init {
        showLoading()

        setOnItemClickListener { _, _, _ ->
            if (ClickUtil.isFastClick()) {
                return@setOnItemClickListener
            }
        }
    }

    fun showLoading() {
        setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.base_view_loading, null))
    }

    fun showEmpty() {
        setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.base_view_empty, null))
    }

    fun showNoNetwork() {
        setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.base_view_no_network, null))
    }

    fun showReload() {
        val noNetWorkView = LayoutInflater.from(mContext).inflate(R.layout.base_view_no_network, null)
        val reloadBtn = noNetWorkView.findViewById<CommonShapeButton>(R.id.reloadBtn)
        reloadBtn?.click {
            onReloadListener?.onReload()
        }
        setEmptyView(noNetWorkView)
    }

}