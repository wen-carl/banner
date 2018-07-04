package com.seven.easybanner.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.seven.easybanner.model.Data

abstract class BaseAdapter(val context: Context, var data: List<Data>) : PagerAdapter() {

    private val mViews: MutableMap<Int, MutableList<View>> = mutableMapOf()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        if (mViews.size < 3) {
            val view = onCreatView(context, position)

        }

        return onCreatView(context, position) as Any
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position, `object`)
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return data.size
    }

    open fun getViewType(position: Int) : Int {
        return -1
    }

    abstract fun onCreatView(context: Context, position: Int) : View?

    abstract fun onDisplay(context: Context, view: View, position: Int, model: Data)
}