package com.wyq.base.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wyq.base.R
import com.wyq.base.util.click

/**
 * 多选列表对话框
 * Created by wyq
 * Date: 2018/10/9
 */
class MultiChooseCheckListDialog private constructor(
    context: Context,
    private val data: ArrayList<String>,
    private var selectedList: ArrayList<Int>?,
    private val titleStr: String?,
    private var cancelStr: String,
    private var confirmStr: String,
    private val onConfirmListener: OnConfirmListener?
) : Dialog(context, R.style.ListDialog) {

    interface OnConfirmListener {
        fun onConfirm(selectedList: ArrayList<Int>)
    }

    internal inner class FilterAdapter(data: ArrayList<String>, selectedList: ArrayList<Int>?) :
        BaseQuickAdapter<String, FilterAdapter.ViewHolder>(R.layout.base_ada_list_dialog_checkbox, data) {

        private val selectedMap = HashMap<Int, String>()

        fun getSelected(): HashMap<Int, String> {
            return selectedMap
        }

        init {
            selectedList?.let {
                for (item in it) {
                    selectedMap[item] = ""
                }
            }
        }

        fun click(position: Int) {
            if (selectedMap.containsKey(position)) {
                selectedMap.remove(position)
            } else{
                selectedMap[position] = ""
            }
            notifyItemChanged(position)
        }

        override fun convert(helper: ViewHolder, item: String) {
            helper.nameTv.text = item
            helper.checkCb.isChecked = selectedMap.containsKey(helper.layoutPosition)
        }

        internal inner class ViewHolder(view: View) : BaseViewHolder(view) {
            internal var nameTv = view.findViewById<TextView>(R.id.nameTv)
            internal var checkCb = view.findViewById<AppCompatCheckBox>(R.id.checkCb)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView = LayoutInflater.from(context).inflate(R.layout.base_dia_multi_choose_list, null)
        setContentView(contentView)

        val filterRv = contentView.findViewById<RecyclerView>(R.id.filterRv)
        (filterRv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//        filterRv.itemAnimator?.changeDuration = 0
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        filterRv.layoutManager = manager
        filterRv.addItemDecoration(
            ItemDivider(
                context,
                1,
                context.resources.getColor(R.color.light_divider)
            )
        )
        val filterAdapter = FilterAdapter(data, selectedList)
        filterRv.adapter = filterAdapter

        filterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
            filterAdapter.click(position)
        }

        val cancelBtn = contentView.findViewById<Button>(R.id.cancelBtn)
        cancelBtn.text = cancelStr
        cancelBtn.click {
            dismiss()
        }
        val confirmBtn = contentView.findViewById<Button>(R.id.confirmBtn)
        confirmBtn.text = confirmStr
        confirmBtn.click {
            if (onConfirmListener != null) {
                val selectedMap = filterAdapter.getSelected()
                val selectedList = ArrayList<Int>()
                for (key in selectedMap.keys) {
                    selectedList.add(key)
                }
                onConfirmListener.onConfirm(selectedList)
            }
            dismiss()
        }

        val titleView = contentView.findViewById<View>(R.id.titleView)
        val titleTv = contentView.findViewById<TextView>(R.id.titleTv)
        if (!TextUtils.isEmpty(titleStr)) {
            titleTv.text = titleStr
        } else {
            titleView.visibility = View.GONE
        }
    }

    class Builder(private val context: Context) {
        private var data = ArrayList<String>()
        private var confirmStr = "确定"
        private var cancelStr = "取消"
        private var selectedList: ArrayList<Int>? = null
        private var titleStr: String? = null
        private var onConfirmListener: OnConfirmListener? = null

        fun setData(data: ArrayList<String>): Builder {
            return setData(data, selectedList)
        }

        fun setData(data: ArrayList<String>, selectedList: ArrayList<Int>?): Builder {
            this.data = data
            this.selectedList = selectedList
            return this
        }

        fun setTitle(titleStr: String): Builder {
            this.titleStr = titleStr
            return this
        }

        fun setCancel(cancelStr: String): Builder {
            this.cancelStr = cancelStr
            return this
        }

        fun setConfirm(confirmStr: String, onConfirmListener: OnConfirmListener): Builder {
            this.confirmStr = confirmStr
            this.onConfirmListener = onConfirmListener
            return this
        }

        fun setConfirm(confirmStr: String, action: (selectedList: ArrayList<Int>) -> Unit): Builder {
            this.confirmStr = confirmStr
            this.onConfirmListener = object :
                OnConfirmListener {
                override fun onConfirm(selectedList: ArrayList<Int>) {
                    action(selectedList)
                }
            }
            return this
        }

        fun setConfirm(action: (selectedList: ArrayList<Int>) -> Unit): Builder {
            this.onConfirmListener = object :
                OnConfirmListener {
                override fun onConfirm(selectedList: ArrayList<Int>) {
                    action(selectedList)
                }
            }
            return this
        }

        fun create(): MultiChooseCheckListDialog {
            return MultiChooseCheckListDialog(
                context,
                data,
                selectedList,
                titleStr,
                cancelStr,
                confirmStr,
                onConfirmListener
            )
        }
    }

}