package com.seven.easybanner

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import com.seven.easybanner.adapter.BaseAdapter
import android.widget.FrameLayout
import com.seven.easybanner.model.Data
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

class EasyBanner(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    private var mViewPager: ViewPager
    private var mTouching = false

    var isAutoPlay = true
        set(value) {
            if (value && status != BannerStatus.AutoPlaying) {
                pause()
                start()
            }

            status = if (value)
                BannerStatus.AutoPlaying
            else
                BannerStatus.ManualPlaying

            field = value
        }

    var timeInterval = 2000L
        set(value) {
            if (value != field) {
                field = value

                start()
            }
        }

    var status = BannerStatus.NotStart
        private set

    var direction = Direction.Positive
    var adapter: BaseAdapter<Data>? = null
        set(value) {
            field = value

            mViewPager.adapter = value
            mViewPager.currentItem = 1
        }

    private var count: Int = 0
        get() = adapter?.data?.size ?: 0

    init {
        val mainContent = LayoutInflater.from(context).inflate(R.layout.layout_easy_banner, this, true)
        mViewPager = mainContent.findViewById(R.id.banner_content)
        mViewPager.addOnPageChangeListener(this)
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

    private var mLoopTask: Job? = null

    private fun getAutoPlayTask() : Job {
        return  launch(UI) {
            repeat(Int.MAX_VALUE) {
                delay(timeInterval)

                if (count > 1 && isAutoPlay && !mTouching) {
                    if (direction == Direction.Positive) {
                        showNext()
                    } else {
                        showPrevious()
                    }
                }
            }
        }
    }

    fun setAdapter(adapter: BaseAdapter<Data>) : EasyBanner {
        this.adapter = adapter

        return this
    }

    fun start() {
        val shouldStart = when (status) {
            BannerStatus.AutoPlaying,
            BannerStatus.Paused,
            BannerStatus.Stoped -> true
            else -> false
        }
        if (isAutoPlay && shouldStart) {
            if (null == mLoopTask) {
                mLoopTask = getAutoPlayTask()
            }

            if (!mLoopTask!!.isActive) {
                mLoopTask!!.start()
            }
        }
    }

    fun stop() {
        pause()
        status = BannerStatus.Stoped
        
        val position = when (direction) {
           Direction.Positive   -> 0
           else                 -> 2
        }

        mViewPager.setCurrentItem(position, false)
        mViewPager.currentItem = 1
    }

    fun pause() {
        mLoopTask?.cancel()
        mLoopTask = null
        status = BannerStatus.Paused
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
        val temp = if (index <= 0) {
            0
        } else if (index <= count + 1) {
            index
        } else {
            count + 1
        }

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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        val action = ev.action
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
            || action == MotionEvent.ACTION_OUTSIDE
        ) {
            mTouching = false
        } else if (action == MotionEvent.ACTION_DOWN) {
            mTouching = true
        }

        return super.dispatchTouchEvent(ev)
    }
}

enum class BannerStatus {
    NotStart,
    AutoPlaying,
    ManualPlaying,
    Paused,
    Stoped
}

enum class Direction {
    Positive,
    Negative
}