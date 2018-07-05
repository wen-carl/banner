package com.seven.easybanner.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.seven.easybanner.model.Data

private const val DEFAULT_TYPE = -1
private const val KEY_UNUSED = -1

abstract class BaseAdapter(val context: Context, data: List<Data>) : PagerAdapter() {

    var mData: List<Data> = emptyList()
        set(value) {
            field = value
            updateMockedData()
        }

    private var mMockData: List<Data> = emptyList()

    init {
        mData = data
    }

    private fun updateMockedData() {
        mMockData = if (mData.size > 1) {
            val temp = mData.toMutableList()
            val first = temp[0]
            val last = temp[temp.size - 1]
            temp.add(0, last)
            temp.add(first)
            temp.toList()
        } else {
            mData
        }

        notifyDataSetChanged()
    }

    private val mCachedViews: MutableMap<Int, MutableMap<Int, View>> = mutableMapOf()
    private val mShowingViews: MutableMap<Int, View> = mutableMapOf()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val type = getViewType(position)
        val tempMap = mCachedViews[type]

        var view: View? = null
        if (null != tempMap) {
            view = tempMap[KEY_UNUSED]
            tempMap.remove(KEY_UNUSED)
        }

        if (null == view) {
            view = onCreatView(context, container, position, type)
        }

        onDisplay(view, position, mMockData[position])

        mShowingViews[position] = view
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position, `object`)
        val view = `object` as View
        container.removeView(view)
        mShowingViews.remove(position)

        val type = getViewType(position)
        var tempMap = mCachedViews[type]
        if (null == tempMap) {
            tempMap = mutableMapOf()
            mCachedViews[type] = tempMap
        }
        tempMap[KEY_UNUSED] = view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return mMockData.size
    }

    open fun getViewType(position: Int) : Int {
        return DEFAULT_TYPE
    }

    abstract fun onCreatView(context: Context, parent: ViewGroup, position: Int, viewType: Int) : View

    abstract fun onDisplay(view: View, position: Int, model: Data)
}