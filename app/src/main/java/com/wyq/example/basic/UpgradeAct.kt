package com.wyq.example.basic

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
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
//        Handler().postDelayed({
//            UpgradeUtils.upgrade(
//                this,
//                showProgress = false,
//                progressCancelable = false,
//                showUpgradeDialog = true
//            )
//        }, 1000)

        val unit = "姑苏区市场监督管理局"
        val tip1 = "检查人员根据《中国人民共和国食品安全法》及其实施条例、《食品生产经营日常监督检查管理办法》的规定，与2019年03月12日对你单位进行了监督检查。本次监督检查按照《餐饮服务日常监督检查要点表》开展，"
        val totalCount = "共检查了（38）项内容；"
        val tip2 = "\n其中："
        val important = "\n重点项7项，"
        val importantTip = "项目序号分别是（5、6、7、8、15、27、30），发现问题（0）项，项目序号分别是（）；"
        val normal = "\n一般项31项，"
        val normalTip = "项目序号分别是（1、2、3、4、9、10、11、12、13、14、16、17、18、19、20、21、22、23、25、26、29、31、32、33、34、35、36、37、38、39、40），发现问题（14）项，项目序号分别是（2、3、4、21、31、32、33、34、35、36、37、38、39、40）；"
        val lack = "\n合理缺项2项，"
        val lackTip = "项目序号分别是（24、28）；"

        val ssb = SpannableStringBuilder()
        ssb.append(unit, UnderlineSpan(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        ssb.append(tip1)
        ssb.append(totalCount, StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        ssb.append(tip2)
        ssb.append(important, StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        ssb.append(importantTip)
        ssb.append(normal, StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        ssb.append(normalTip)
        ssb.append(lack, StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        ssb.append(lackTip)

        contentTv.text = ssb
    }

    override fun initLayout(): Int {
        return R.layout.act_upgrade
    }

    override fun overStatusBar(): Boolean {
        return false
    }

}