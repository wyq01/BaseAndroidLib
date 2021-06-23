package com.ts.base.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import android.widget.TextView

import com.ts.base.R

/**
 * loading页
 * Created by Administrator on 2016/9/29.
 */
class BaseProgressDialog private constructor(context: Context?) : Dialog(context, R.style.progressDialog) {

    //    private ImageView loadingIv;
    //    private Animation operatingAnim;
    private val msgTv: TextView

    init {
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.base_dia_progress)
        msgTv = findViewById(R.id.msgTv)
        if (TextUtils.isEmpty(message)) {
            msgTv.visibility = View.GONE
        } else {
            msgTv.visibility = View.VISIBLE
            msgTv.text = message
        }
        //        loadingIv = findViewById(R.id.loadingIv);
        //        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.base_anim_dialog_loading);
        //        LinearInterpolator lin = new LinearInterpolator();
        //        operatingAnim.setInterpolator(lin);
        //        loadingIv.startAnimation(operatingAnim);
    }

    override fun show() {
        //        loadingIv.startAnimation(operatingAnim);
        if (!this.isShowing) {
            super.show()
        }
    }

    override fun dismiss() {
        //        loadingIv.clearAnimation();
        if (this.isShowing) {
            super.dismiss()
        }
    }

    companion object {
        private var baseProgressDialog: BaseProgressDialog? = null
        private var message: String? = null

        fun showDialog(context: Context, cancelable: Boolean): BaseProgressDialog? {
            return showDialog(
                context,
                "",
                cancelable,
                null
            )
        }

        @JvmOverloads
        fun showDialog(context: Context?, msg: String?, cancelable: Boolean = false, l: DialogInterface.OnDismissListener? = null): BaseProgressDialog? {
            message = msg
            try {
                hideDialog()
                baseProgressDialog =
                    BaseProgressDialog(context)
                baseProgressDialog?.setCancelable(cancelable)
                baseProgressDialog?.setCanceledOnTouchOutside(false)
                if (l != null) {
                    baseProgressDialog?.setOnDismissListener(l)
                }
                if (context is Activity && context.isFinishing) {
                    baseProgressDialog = null
                } else {
                    baseProgressDialog?.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            return baseProgressDialog
        }

        fun hideDialog() {
            try {
                if (baseProgressDialog != null && baseProgressDialog!!.isShowing) {
                    baseProgressDialog?.setOnDismissListener(null)
                    baseProgressDialog?.dismiss()
                    baseProgressDialog = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}