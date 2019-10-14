package com.wyq.example.basic

import android.content.Context
import android.content.Intent
import com.ts.upgrade.UpgradeUtils
import com.wyq.base.BaseActivity
import com.wyq.base.util.click
import com.wyq.example.R
import kotlinx.android.synthetic.main.act_upgrade.*

class UpgradeAct : BaseActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, UpgradeAct::class.java)
            context.startActivity(intent)
        }
    }

    override fun initViews() {
        super.initViews()

        setBackgroundTransparent()

        checkUpgradeBtn.click {
            UpgradeUtils.upgrade(
                this,
                showProgress = true,
                progressCancelable = true,
                showUpgradeDialog = true
            )
        }
    }

    override fun initLayout(): Int {
        return R.layout.act_upgrade
    }

    override fun overStatusBar(): Boolean {
        return false
    }

}