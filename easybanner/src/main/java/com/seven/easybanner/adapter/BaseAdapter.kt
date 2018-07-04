package com.seven.easybanner.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.seven.easybanner.model.Data

private const val DEFAULT_TYPE = -1
private const val POSITION_LEFT = 0
private const val POSITION_CENTER = 1
private const val POSITION_RIGHT = 2
private const val POSITION_UNUSED = 3

abstract class BaseAdapter(val context: Context, var data: List<Data>) : PagerAdapter() {

    private val mViews: MutableMap<Int, MutableMap<Int, View>> = mutableMapOf()
    private val mPosition = mutableListOf<Int>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val type = getViewType(position)
        var tempMap = mViews[type]

        if (null == tempMap) {
            tempMap = mutableMapOf()
            mViews[type] = tempMap
        }

        val view: View
        if (tempMap.size < 3) {
            view = onCreatView(context, position, type)

        } else {
            view = tempMap[POSITION_UNUSED]!!
        }

        val left = tempMap[POSITION_LEFT]
        val center = tempMap[POSITION_CENTER]
        val right = tempMap[POSITION_RIGHT]
        onDisplay(context, view, position, data[position])

        return view
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
        return DEFAULT_TYPE
    }

    abstract fun onCreatView(context: Context, position: Int, viewType: Int) : View

    abstract fun onDisplay(context: Context, view: View, position: Int, model: Data)
}