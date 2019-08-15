package com.wyq.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 分割线
 * 默认分割线：高度为2px，颜色为灰色
 * Created by wyq on 2018/1/4.
 * @param context
 */
class GridItemDivider(context: Context) : RecyclerView.ItemDecoration() {

    private lateinit var mPaint: Paint
    private var mDivider: Drawable? = null
    private var mDividerHeight = 2//分割线高度，默认为1px

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param drawableId  分割线图片
     */
    constructor(context: Context, drawableId: Int) : this(context) {
        mDivider = ContextCompat.getDrawable(context, drawableId)
        mDividerHeight = mDivider!!.intrinsicHeight
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    constructor(context: Context, dividerHeight: Int, dividerColor: Int) : this(context) {
        mDividerHeight = dividerHeight
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = dividerColor
        mPaint.style = Paint.Style.FILL
    }

    // 获取分割线尺寸
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        //        outRect.set(0, 0, 0, mDividerHeight);
        val position = parent.getChildLayoutPosition(view)
        //        int lastPosition = state.getItemCount() - 1;
        //        if (position == lastPosition) {
        //            outRect.set(0, 0, 0, 0);
        //        } else {
        //            if (mOrientation == LinearLayoutManager.VERTICAL) {
        //                outRect.set(0, 0, 0, mDividerHeight);
        //            } else {
        //                outRect.set(0, 0, mDividerHeight, 0);
        //            }
        //        }

        var right = mDividerHeight
        var bottom = mDividerHeight
        if (isLastSpan(position, parent)) {
            right = 0
        }
        if (isLastRow(position, parent)) {
            bottom = 0
        }
        outRect.set(0, 0, right, bottom)
    }

    // 绘制分割线
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        mDivider?.let {
            drawHorizontal(c, parent)
            drawVertical(c, parent)
        }
    }

    // 绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.measuredWidth - parent.paddingRight
        val childSize = parent.childCount
        for (i in 0 until childSize - 1) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + mDividerHeight
            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(canvas)
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }

    // 绘制纵向 item 分割线
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.measuredHeight - parent.paddingBottom
        val childSize = parent.childCount
        for (i in 0 until childSize - 1) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + layoutParams.rightMargin
            val right = left + mDividerHeight
            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(canvas)
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }

    private fun isLastRow(itemPosition: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            val itemCount = parent.adapter!!.itemCount
            if (itemCount - itemPosition - 1 < spanCount)
                return true
        }
        return false
    }

    private fun isLastSpan(itemPosition: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            if ((itemPosition + 1) % spanCount == 0)
                return true
        }
        return false
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

}