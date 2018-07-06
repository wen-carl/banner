package com.seven.easybanner.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.seven.easybanner.model.Data

private const val DEFAULT_TYPE = -1
private const val KEY_UNUSED = -1

abstract class BaseAdapter<T>(val context: Context, data: List<T>) : PagerAdapter() where T : Data {

    var data: List<T> = emptyList()
        set(value) {
            field = value
            updateMockedData()
        }

    private var mMockData: List<T> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val mCachedViewHolders: MutableMap<Int, MutableMap<Int, View>> = mutableMapOf()
    private val mShowingViewHolders: MutableMap<Int, View> = mutableMapOf()

    private var mOnClickListener: OnBannerClickListener? = null
    private var mOnLongClickListener: OnBannerLongClickListener? = null

    init {
        this.data = data
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val type = getViewType(position)
        val tempMap = mCachedViewHolders[type]

        var holder: View? = null
        if (null != tempMap) {
            holder = tempMap[KEY_UNUSED]
            tempMap.remove(KEY_UNUSED)
        }

        if (null == holder) {
            holder = onCreateView(context, container, getRealPosition(position), type)
        }

        container.addView(holder)
        onDisplay(holder, getRealPosition(position), mMockData[position])

        mShowingViewHolders[position] = holder

        holder.tag = position
        setClickListener(holder, position)

        return holder
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val holder = `object` as View
        container.removeView(holder)
        mShowingViewHolders.remove(position)

        val type = getViewType(position)
        var tempMap = mCachedViewHolders[type]
        if (null == tempMap) {
            tempMap = mutableMapOf()
            mCachedViewHolders[type] = tempMap
        }
        tempMap[type] = holder
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

    abstract fun onCreateView(context: Context, parent: ViewGroup, position: Int, viewType: Int) : View

    abstract fun onDisplay(holder: View, position: Int, model: T)

    fun setOnBannerClickListener(listener: OnBannerClickListener) : BaseAdapter<T> {
        mOnClickListener = listener

        return this
    }

    fun setOnBannerLongClickListener(listener: OnBannerLongClickListener) : BaseAdapter<T> {
        mOnLongClickListener = listener

        return this
    }

    private fun setClickListener(holder: View, position: Int) {
        if (!holder.hasOnClickListeners()) {
            holder.setOnClickListener {
                val real = getRealPosition(it.tag as Int)
                mOnClickListener?.onBannerClicked(it, real, data[real])

                mOnLongClickListener?.onBannerLongClicked(it, position, data[real])
            }
        }
    }

    private fun getRealPosition(position: Int) : Int {
        var real = 0
        if (data.size <= 1) {
            real = 0
        } else {
            real = (position - 1) % data.size
            if (real < 0) {
                real += data.size
            }
        }

        return real
    }

    private fun updateMockedData() {
        mMockData = if (data.size > 1) {
            val temp = data.toMutableList()
            val first = temp[0]
            val last = temp[temp.size - 1]
            temp.add(0, last)
            temp.add(first)
            temp.toList()
        } else {
            data
        }
    }

    fun bindData(data: List<T>) {
        this.data = data
    }

    fun add(index: Int, model: T) {
        val temp = data.toMutableList()
        temp.add(index, model)
        this.data = temp.toList()
    }

    fun add(model: T) : Boolean {
        val temp = data.toMutableList()
        val result = temp.add(model)
        if (result) {
            this.data = temp.toList()
        }

        return result
    }

    fun add(index: Int, list: List<T>) : Boolean {
        val temp = data.toMutableList()
        val result = temp.addAll(index, list)
        if (result) {
            this.data = temp.toList()
        }

        return result
    }

    fun add(list: List<T>) : Boolean {
        val temp = data.toMutableList()
        val result = temp.addAll(list)
        if (result) {
            this.data = temp.toList()
        }

        return result
    }

    fun remove(index: Int) : T {
        val temp = data.toMutableList()
        val result = temp.removeAt(index)
        this.data = temp.toList()

        return result
    }

    fun remove(list: List<T>) : Boolean {
        val temp = data.toMutableList()
        val result = temp.removeAll(list)
        if (result) {
            this.data = temp.toList()
        }

        return result
    }

    fun clear() {
        val temp = data.toMutableList()
        val result = temp.clear()
        this.data = temp.toList()

        return result
    }
}

//open class ViewHolder(val itemView: View) {
//    var position: Int = -1
//}

interface OnBannerClickListener {
    fun onBannerClicked(view: View, position: Int, model: Data)
}

interface OnBannerLongClickListener {
    fun onBannerLongClicked(view: View, position: Int, model: Data)
}
