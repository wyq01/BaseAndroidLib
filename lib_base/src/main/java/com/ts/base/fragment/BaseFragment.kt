package com.ts.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.blankj.utilcode.util.KeyboardUtils
import com.ts.base.R
import com.ts.base.event.BaseEvent
import com.ts.base.view.LoadingLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 懒加载fragment
 */
abstract class BaseFragment : IBaseFragment() {
    /**
     * 是否可见状态
     */
    private var isUiVisible = false
    /**
     * 标志位，View已经初始化完成。
     */
    private var isPrepared = false
    /**
     * 是否第一次加载
     */
    private var isFirstLoad = true

    /**
     * view是否加载完成
     */
    private var isViewInit = false

    protected var titleTv: TextView? = null
    protected var msgTv: TextView? = null
    protected var rightBtn: ImageButton? = null

    private lateinit var rootView : View
    private var layoutContainer: FrameLayout? = null

    private var loadingLayout: LoadingLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBaseEvent(event: BaseEvent) {}

    override fun onDestroy() {
        super.onDestroy()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        val activity = activity
        if (activity != null) {
            KeyboardUtils.hideSoftInput(activity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initData(arguments)
        isFirstLoad = true
        rootView = customContentView(inflater.inflate(R.layout.base_fm_base, null), inflater)

        isPrepared = true
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isViewInit = true
        initViews(rootView)
        if (isUiVisible) {
            onVisible()
        }
    }

    private fun customContentView(rootView: View, inflater: LayoutInflater): View {
        val contentView = inflater.inflate(initLayout(), null)
        if (contentView != null) {
            loadingLayout = contentView.findViewById(R.id.loadingLayout)
            loadingLayout?.let {
                showLoading()
                loadingLayout?.setOnRetryListener {
                    showLoading()
                    retryLoad()
                }
            }
            layoutContainer = rootView.findViewById(R.id.layoutContainer)
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            layoutContainer?.addView(contentView, params)

            titleTv = contentView.findViewById(R.id.titleTv)
            msgTv = contentView.findViewById(R.id.msgTv)
            rightBtn = contentView.findViewById(R.id.rightBtn)
        }
        return rootView
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            isUiVisible = true
            if (isViewInit)
                onVisible()
        } else {
            isUiVisible = false
            if (isViewInit)
                onInvisible()
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            isUiVisible = true
            if (isViewInit)
                onVisible()
        } else {
            isUiVisible = false
            if (isViewInit)
                onInvisible()
        }
    }

    override fun onVisible() {
        lazyLoad()
    }

    override fun onInvisible() {}

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    private fun lazyLoad() {
        if (!isPrepared || !isUiVisible || !isFirstLoad || !isViewInit) {
            return
        }
        getData()
        isFirstLoad = false
    }

    /** 设置根布局透明 */
    protected fun setBackgroundTransparent() {
        context?.let {
            layoutContainer?.setBackgroundColor(it.resources.getColor(android.R.color.transparent))
        }
    }

    /** 显示空页面 */
    protected fun showEmpty() {
        loadingLayout?.showEmpty()
    }

    /** 显示加载框 */
    protected fun showLoading() {
        loadingLayout?.showLoading()
    }

    /** 显示无网络页面 */
    protected fun showNoNetwork() {
        loadingLayout?.showNoNetwork()
    }

    /** 显示内容 */
    protected fun showContent() {
        loadingLayout?.showContent()
    }

    /** 重新加载数据 */
    protected open fun retryLoad() {}

}