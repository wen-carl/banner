package com.seven.easybanner

import android.content.Context
import android.os.Handler
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import com.seven.easybanner.adapter.BaseAdapter
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min

class EasyBanner(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    private var mViewPager: ViewPager
    var isAutoPlay = true
    var timeInterval = 2000L
    var direction = Direction.Positive
    var mBaseAdapter: BaseAdapter? = null

    private var count: Int = 0
        get() = mBaseAdapter?.mData?.size ?: 0

    init {
        val mainContent = LayoutInflater.from(context).inflate(R.layout.layout_easy_banner, this, true)
        mViewPager = mainContent.findViewById(R.id.banner_content)
        mViewPager.addOnPageChangeListener(this)
    }

    private val mHandler = Handler()
    private val mRunnable = Runnable {
        if (direction == Direction.Positive) {
            showNext()
        } else {
            showPrevious()
        }

        mHandler.postDelayed(mRunnable, timeInterval)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    fun setAutoPlay(enable: Boolean) : EasyBanner {
        isAutoPlay = enable
        return this
    }

    fun setTimeInterval(interval: Long) : EasyBanner {
        timeInterval = interval
        return this
    }

    fun setAdapter(adapter: BaseAdapter) : EasyBanner {
        mBaseAdapter = adapter
        mViewPager.adapter = mBaseAdapter
        mViewPager.currentItem = 1
        return this
    }

    fun start() {

    }

    fun stop() {

    }

    fun pause() {

    }

    fun showPrevious() {
        val index = if (mViewPager.currentItem == 0) {
            count
        } else {
            mViewPager.currentItem - 1
        }

        show(index)
    }

    fun showNext() {
        val index = if (mViewPager.currentItem == count + 1) {
            1
        } else {
            mViewPager.currentItem + 1
        }

        show(index)
    }

    fun show(index: Int) {
        val temp = min(count, max(1, index))
        mViewPager.currentItem = temp
    }

    /**
     * ViewPager.OnPageChangeListener
     *
     * data:        [0,1,2]
     * mock data:   [2,0,1,2,0]
     *
     * For loop scroll
     */
    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_IDLE,
            ViewPager.SCROLL_STATE_DRAGGING -> {
                if (mViewPager.currentItem == 0) {
                    mViewPager.setCurrentItem(count, false)
                } else if (mViewPager.currentItem == count + 1) {
                    mViewPager.setCurrentItem(1, false)
                }
            }
            ViewPager.SCROLL_STATE_SETTLING -> {

            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {}
}

enum class Direction {
    Positive,
    Negative
}