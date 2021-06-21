package com.ts.base.adapter

import android.content.Context
import android.view.LayoutInflater
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ts.base.R
import com.ts.base.util.ClickUtil
import com.ts.base.util.click
import com.ts.base.view.CommonShapeButton

/**
 *
 * Created by ts
 * Date: 2019/1/14
 */
abstract class BaseMultiItemAda<T : MultiItemEntity, K : BaseViewHolder>(
    private var mContext: Context?,
    layoutResId: Int,
    data: MutableList<T>?
) : BaseMultiItemQuickAdapter<T, K>(data) {
    interface OnReloadListener {
        fun onReload()
    }

    fun setOnReloadListener(l: OnReloadListener) {
        this.onReloadListener = l
    }

    fun setOnReloadListener(action: () -> Unit) {
        this.onReloadListener = object :
            OnReloadListener {
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
        setOnItemClickListener { _, _, _ ->
            if (ClickUtil.isFastClick()) {
                return@setOnItemClickListener
            }
        }
    }

    fun showLoading() {
        mContext?.let {
            setEmptyView(LayoutInflater.from(it).inflate(R.layout.base_view_loading, null))
        }
    }

    fun showEmpty() {
        mContext?.let {
            setEmptyView(LayoutInflater.from(it).inflate(R.layout.base_view_empty, null))
        }
    }

    fun showNoNetwork() {
        mContext?.let {
            setEmptyView(LayoutInflater.from(it).inflate(R.layout.base_view_no_network, null))
        }
    }

    fun showReload() {
        mContext?.let {
            val noNetWorkView = LayoutInflater.from(it).inflate(R.layout.base_view_no_network, null)
            val reloadBtn = noNetWorkView.findViewById<CommonShapeButton>(R.id.reloadBtn)
            reloadBtn?.click {
                onReloadListener?.onReload()
            }
            setEmptyView(noNetWorkView)
        }
    }

}