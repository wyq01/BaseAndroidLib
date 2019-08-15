package com.wyq.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 分割线
 * @param context
 * @param dividerHeight 分割线高度
 * Created by wyq on 2018/1/4.
 */
class VerticalItemDivider(context: Context?, var dividerHeight: Int) : RecyclerView.ItemDecoration() {

    private var mPaint: Paint? = null

    constructor(context: Context, dividerHeight: Int, dividerColor: Int) : this(context, dividerHeight) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.color = dividerColor
        mPaint?.style = Paint.Style.FILL
    }

    // 获取分割线尺寸
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildLayoutPosition(view)
        if (position == 0) {
            outRect.set(0, dividerHeight, 0, dividerHeight)
        } else {
            outRect.set(0, 0, 0, dividerHeight)
        }
    }

    // 绘制分割线
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        mPaint?.let {
            drawHorizontal(c, parent)
        }
    }

    // 绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.measuredWidth - parent.paddingRight
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + dividerHeight
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

            if (i == 0) {
                val top = child.top + layoutParams.topMargin
                val bottom = top + dividerHeight
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
            }
        }
    }

}