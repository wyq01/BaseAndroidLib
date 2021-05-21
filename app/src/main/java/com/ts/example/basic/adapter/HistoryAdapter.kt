package com.ts.example.basic.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener
import com.chad.library.adapter.base.BaseViewHolder
import com.ts.example.R
import com.ts.example.basic.WebViewAct
import com.ts.base.adapter.BaseAda
import com.ts.base.util.ClickUtil

class HistoryAdapter(context: Context?, data: List<String>?) :
        BaseAda<String, HistoryAdapter.ViewHolder>(context, R.layout.ada_history, data) {

    init {
        onItemClickListener = OnItemClickListener { _, _, position ->
            if (ClickUtil.isFastClick()) {
                return@OnItemClickListener
            }
            val item = getItem(position)
            item?.let {
                WebViewAct.startActivity(mContext, it)
            }
        }
    }

    override fun convert(viewHolder: ViewHolder, item: String) {
        viewHolder.urlTv.text = item
    }

    inner class ViewHolder(view: View) : BaseViewHolder(view) {
        internal val urlTv = view.findViewById<TextView>(R.id.urlTv)
    }

}