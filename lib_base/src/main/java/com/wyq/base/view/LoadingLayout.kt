package com.wyq.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.wyq.base.R

/**
 * Created by wyq
 * Date: 2018/10/29
 */
class LoadingLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) :
    FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 空数据View
     */
    private var mEmptyView: Int = 0
    /**
     * 状态View
     */
    private var mNoNetworkView: Int = 0
    /**
     * 加载View
     */
    private var mLoadingView: Int = 0

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingLayout, 0, 0)
        try {
            mNoNetworkView = a.getResourceId(R.styleable.LoadingLayout_noNetworkView, R.layout.base_view_no_network)
            mLoadingView = a.getResourceId(R.styleable.LoadingLayout_loadingView, R.layout.base_view_loading)
            mEmptyView = a.getResourceId(R.styleable.LoadingLayout_noDataView, R.layout.base_view_empty)
            val inflater = LayoutInflater.from(getContext())
            inflater.inflate(mNoNetworkView, this, true)
            inflater.inflate(mLoadingView, this, true)
            inflater.inflate(mEmptyView, this, true)
        } finally {
            a.recycle()
        }
    }

    /**
     * 布局加载完成后隐藏所有View
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0 until childCount - 1) {
            getChildAt(i).visibility = View.INVISIBLE
        }
    }

    /**
     * State View
     */
    fun showNoNetwork() {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            if (i == 0) {
                child.visibility = View.VISIBLE
            } else {
                child.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Loading view
     */
    fun showLoading() {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            if (i == 1) {
                child.visibility = View.VISIBLE
            } else {
                child.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Empty view
     */
    fun showEmpty() {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            if (i == 2) {
                child.visibility = View.VISIBLE
            } else {
                child.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * 展示内容
     */
    fun showContent() {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            if (i > 2) {
                child.visibility = View.VISIBLE
            } else {
                child.visibility = View.INVISIBLE
            }
        }
    }

    fun showNone() {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            child.visibility = View.INVISIBLE
        }
    }

    fun setOnRetryListener(l: View.OnClickListener) {
        val reloadBtn = this.getChildAt(0).findViewById<View>(R.id.reloadBtn)
        reloadBtn?.setOnClickListener(l)
    }

    fun setOnRetryListener(action: () -> Unit) {
        val reloadBtn = this.getChildAt(0).findViewById<View>(R.id.reloadBtn)
        reloadBtn?.setOnClickListener { action() }
    }

}