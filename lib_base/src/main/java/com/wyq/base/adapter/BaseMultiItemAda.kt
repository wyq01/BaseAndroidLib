package com.wyq.base.adapter

import android.content.Context
import android.view.LayoutInflater
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.wyq.base.R
import com.wyq.base.util.ClickUtil
import com.wyq.base.util.click
import com.wyq.base.view.CommonShapeButton

/**
 *
 * Created by wyq
 * Date: 2019/1/14
 */
abstract class BaseMultiItemAda<T : MultiItemEntity, K : BaseViewHolder> :
    BaseMultiItemQuickAdapter<T, K> {
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

    private var context: Context? = null
    private var onReloadListener: OnReloadListener? = null

    fun updateItem(position: Int, item: T) {
        data[position] = item
        notifyItemChanged(position)
    }

    init {
        onItemClickListener = OnItemClickListener { _, _, _ ->
            if (ClickUtil.isFastClick()) {
                return@OnItemClickListener
            }
        }
    }

    constructor(context: Context?, data: List<T>?) : super(data) {
        this.context = context
        showLoading()
    }

    fun showLoading() {
        context?.let {
            emptyView = LayoutInflater.from(it).inflate(R.layout.base_view_loading, null)
        }
    }

    fun showEmpty() {
        context?.let {
            emptyView = LayoutInflater.from(it).inflate(R.layout.base_view_empty, null)
        }
    }

    fun showNoNetwork() {
        context?.let {
            emptyView = LayoutInflater.from(it).inflate(R.layout.base_view_no_network, null)
        }
    }

    fun showReload() {
        context?.let {
            val noNetWorkView = LayoutInflater.from(it).inflate(R.layout.base_view_no_network, null)
            val reloadBtn = noNetWorkView.findViewById<CommonShapeButton>(R.id.reloadBtn)
            reloadBtn?.click {
                onReloadListener?.onReload()
            }
            emptyView = noNetWorkView
        }
    }

}