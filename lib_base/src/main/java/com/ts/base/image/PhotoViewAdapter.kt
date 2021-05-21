package com.ts.base.image

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView
import com.ts.base.R
import com.ts.base.util.ImageLoader
import com.ts.base.view.LoadingLayout

class PhotoViewAdapter(var context: Context, var data: ArrayList<String>) : PagerAdapter() {

    interface OnFinishListener {
        fun onFinish()
    }

    private var onFinishListener: OnFinishListener? = null
    fun setOnFinishListener(action: () -> Unit) {
        this.onFinishListener = object :
            OnFinishListener {
            override fun onFinish() {
                action()
            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(context).inflate(R.layout.base_ada_photo_view, null)
        val loadingLayout = itemView.findViewById<LoadingLayout>(R.id.loadingLayout)
        val photoView = itemView.findViewById<PhotoView>(R.id.photoView)
        photoView.setOnPhotoTapListener { _, _, _ ->
            onFinishListener?.onFinish()
        }
        loadingLayout.showContent()
        ImageLoader.load(context, data[position], photoView)
//        Glide.with(context)
//            .load(data[position])
//            .listener(object : RequestListener<String, GlideDrawable> {
//                override fun onResourceReady(
//                    p0: GlideDrawable?,
//                    p1: String?,
//                    p2: Target<GlideDrawable>?,
//                    p3: Boolean,
//                    p4: Boolean
//                ): Boolean {
//                    loadingLayout.showContent()
//                    return true
//                }
//                override fun onException(
//                    p0: Exception?,
//                    p1: String?,
//                    p2: Target<GlideDrawable>?,
//                    p3: Boolean
//                ): Boolean {
//                    loadingLayout.showEmpty()
//                    return true
//                }
//            }).into(photoView)

        container.addView(itemView)
        return itemView
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

}