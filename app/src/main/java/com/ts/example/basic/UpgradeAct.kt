package com.ts.example.basic

import android.content.Context
import android.content.Intent
import com.ts.base.activity.BaseActivity
import com.ts.upgrade.UpgradeUtils
import com.ts.base.activity.IBaseActivity
import com.ts.base.util.click
import com.ts.example.R
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