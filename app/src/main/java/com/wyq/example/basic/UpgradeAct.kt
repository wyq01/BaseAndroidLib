package com.wyq.example.basic

import android.content.Context
import android.content.Intent
import android.os.Handler
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

        checkUpgradeBtn.click {
            UpgradeUtils.upgrade(
                this,
                showProgress = true,
                progressCancelable = true,
                showUpgradeDialog = true
            )
        }
        Handler().postDelayed({
            UpgradeUtils.upgrade(
                this,
                showProgress = false,
                progressCancelable = false,
                showUpgradeDialog = true
            )
        }, 1000)
    }

    override fun initLayout(): Int {
        return R.layout.act_upgrade
    }

    override fun overStatusBar(): Boolean {
        return false
    }

}