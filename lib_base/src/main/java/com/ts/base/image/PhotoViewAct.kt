package com.ts.base.image

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.ts.base.R
import com.ts.base.activity.BaseActivity
import kotlinx.android.synthetic.main.base_act_photo_view.*
import java.util.*

class PhotoViewAct : BaseActivity() {

    companion object {
        const val EXTRA_KEY_IMAGES = "images"
        const val EXTRA_KEY_SHOW_INDEX = "index"

        @JvmStatic
        fun startActivity(context: Context, photo: String) {
            val images = ArrayList<String>()
            images.add(photo)
            startActivity(context, images, false)
        }

        @JvmStatic
        fun startActivity(context: Context, photos: ArrayList<String>) {
            startActivity(context, photos, false)
        }

        @JvmStatic
        fun startActivity(context: Context, photos: ArrayList<String>, showIndex: Boolean) {
            val intent = Intent(context, PhotoViewAct::class.java)
            intent.putExtra(EXTRA_KEY_IMAGES, photos)
            intent.putExtra(EXTRA_KEY_SHOW_INDEX, showIndex)
            context.startActivity(intent)
        }
    }

    private var data: ArrayList<String> = ArrayList()
    private var showIndex = false // 是否显示序号
    private var photoViewAdapter: PhotoViewAdapter? = null

    override fun initData(intent: Intent) {
        super.initData(intent)

        val images = intent.getStringArrayListExtra(EXTRA_KEY_IMAGES)
        images?.let {
            data.addAll(it)
        }

        showIndex = intent.getBooleanExtra(EXTRA_KEY_SHOW_INDEX, false)
    }

    override fun initViews() {
        super.initViews()

        mImmersionBar?.statusBarColor(android.R.color.black)
            ?.statusBarDarkFont(false)
            ?.init()

        if (showIndex) {
            indexTv.visibility = View.VISIBLE
            indexTv.text = String.format("1/${data.size}")
        } else {
            indexTv.visibility = View.INVISIBLE
        }

        photoViewAdapter = PhotoViewAdapter(this, data)
        photoViewAdapter?.setOnFinishListener {
            finish()
        }
        viewPager.adapter = photoViewAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (showIndex) {
                    indexTv.text = String.format("${position + 1}/${data.size}")
                }
            }
        })
    }

    override fun initLayout(): Int {
        return R.layout.base_act_photo_view
    }

    override fun overStatusBar(): Boolean {
        return true
    }
}