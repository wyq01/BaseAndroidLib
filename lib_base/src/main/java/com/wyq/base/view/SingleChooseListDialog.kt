package com.wyq.base.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

import com.wyq.base.R

/**
 * 单选列表对话框
 * Created by wyq
 * Date: 2018/10/9
 */
class SingleChooseListDialog private constructor(
    context: Context,
    private val data: List<String>?,
    private var checkPos: Int,
    private val titleStr: String?,
    private val onItemClickListener: OnItemClickListener?
) : Dialog(context, R.style.ListDialog) {
//    private String btnStr;
//    private OnFilterListener onFilterListener;
//        this.btnStr = btnStr;
//        this.onFilterListener = onFilterListener;
    //    public interface OnFilterListener {
    //        void filter(int pos);
    //    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    internal inner class FilterAdapter(data: List<String>, private var checkPos: Int) :
        BaseQuickAdapter<String, FilterAdapter.ViewHolder>(R.layout.base_ada_list_dialog, data) {

        fun updateCheckPos(checkPos: Int) {
            this.checkPos = checkPos
            notifyItemChanged(checkPos)
        }

        override fun convert(helper: ViewHolder, item: String) {
            helper.nameTv.text = item
            helper.checkIv.visibility =
                if (helper.layoutPosition == checkPos) View.VISIBLE else View.INVISIBLE
        }

        internal inner class ViewHolder(view: View) : BaseViewHolder(view) {
            internal var nameTv = view.findViewById<TextView>(R.id.nameTv)
            internal var checkIv = view.findViewById<ImageView>(R.id.checkIv)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView =
            LayoutInflater.from(context).inflate(R.layout.base_dia_single_choose_list, null)
        setContentView(contentView)

        val filterRv = contentView.findViewById<RecyclerView>(R.id.filterRv)
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
        data?.let {
            val filterAdapter = FilterAdapter(it, checkPos)
            filterRv.adapter = filterAdapter
            // 解决notifyItemChanged闪烁问题
            (filterRv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            filterAdapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    onItemClickListener?.onClick(position)
                    checkPos = position
                    filterAdapter.updateCheckPos(position)
                    dismiss()
                }
        }

        //        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                dismiss();
        //            }
        //        });

        val titleView = contentView.findViewById<View>(R.id.titleView)
        val titleTv = contentView.findViewById<TextView>(R.id.titleTv)
        if (!TextUtils.isEmpty(titleStr)) {
            titleTv.text = titleStr
        } else {
            titleView.visibility = View.GONE
        }

        //        Window window = this.getWindow();
        //        window.setGravity(Gravity.BOTTOM);
        //        WindowManager.LayoutParams params = window.getAttributes();
        //        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        //        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //        window.setAttributes(params);
    }

    class Builder(private val context: Context) {
        //        private String btnStr;
        //        private OnFilterListener onFilterListener;

        private var data: List<String>? = null
        private var checkPos: Int = 0
        private var titleStr: String? = null
        private var onItemClickListener: OnItemClickListener? = null

        fun setTitle(titleStr: String): Builder {
            this.titleStr = titleStr
            return this
        }

        fun setData(data: List<String>): Builder {
            return setData(data, -1)
        }

        fun setData(data: List<String>, checkPos: Int): Builder {
            this.data = data
            this.checkPos = checkPos
            return this
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener): Builder {
            this.onItemClickListener = onItemClickListener
            return this
        }

        fun setOnItemClickListener(action: (position: Int) -> Unit): Builder {
            this.onItemClickListener = object :
                OnItemClickListener {
                override fun onClick(position: Int) {
                    action(position)
                }
            }
            return this
        }
        //        public Builder setBtn(String btnStr) {
        //            this.btnStr = btnStr;
        //            return this;
        //        }
        //
        //        public Builder setBtn(String btnStr, OnFilterListener onFilterListener) {
        //            this.btnStr = btnStr;
        //            this.onFilterListener = onFilterListener;
        //            return this;
        //        }
        //
        //        public Builder setOnFilterListener(OnFilterListener onFilterListener) {
        //            this.onFilterListener = onFilterListener;
        //            return this;
        //        }

        fun create(): SingleChooseListDialog {
            return SingleChooseListDialog(
                context,
                data,
                checkPos,
                titleStr,
                onItemClickListener
            )
        }

    }

}