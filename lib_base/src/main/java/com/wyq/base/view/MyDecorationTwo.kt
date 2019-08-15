package com.wyq.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

class MyDecorationTwo(context: Context) : RecyclerView.ItemDecoration() {
//        int attrs[] = new int[]{android.R.attr.listDivider};
//        TypedArray a = context.obtainStyledAttributes(attrs);
//        mDivider = a.getDrawable(0);
//        a.recycle();
//        mDivider = context.getResources().getDrawable(R.drawable.divider);

    private val mDivider: Drawable? = null

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        drawVertical(c, parent)
        drawHorizontal(c, parent)
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val top = child.top - params.topMargin
            val right = left + mDivider!!.intrinsicWidth
            val bottom = child.bottom + params.bottomMargin
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val top = child.bottom + params.bottomMargin
            val right = child.right + params.rightMargin
            val bottom = top + mDivider!!.minimumHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        super.getItemOffsets(outRect, itemPosition, parent)
        var right = mDivider!!.intrinsicWidth
        var bottom = mDivider.intrinsicHeight

        if (isLastSpan(itemPosition, parent)) {
            right = 0
        }

        if (isLastRow(itemPosition, parent)) {
            bottom = 0
        }
        outRect.set(0, 0, right, bottom)
    }

    //    @Override
    //    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    //        super.getItemOffsets(outRect, view, parent, state);
    //        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
    //    }

    fun isLastRow(itemPosition: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            val itemCount = parent.adapter!!.itemCount
            if (itemCount - itemPosition - 1 < spanCount)
                return true
        }
        return false
    }

    fun isLastSpan(itemPosition: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            if ((itemPosition + 1) % spanCount == 0)
                return true
        }
        return false
    }
}
