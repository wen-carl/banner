package com.seven.easybanner

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seven.easybanner.adapter.BaseAdapter
import android.widget.FrameLayout

class EasyBanner(private val build: Build, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(build.context, attrs, defStyleAttr) {

    private lateinit var mViewPager: ViewPager

    init {
        val mainContent = LayoutInflater.from(build.context).inflate(R.layout.layout_easy_banner, this, true)
        mViewPager = mainContent.findViewById(R.id.banner_content)
        mViewPager.adapter = build.mAdapter
    }

    fun start() {

    }

    fun stop() {

    }

    fun pause() {

    }

    fun showPrevious() {

    }

    fun showNext() {

    }

    fun show(index: Int) {

    }

    class Build(val context: Context) {

        var isAutoPlay = true
        var timeInterval = 2.0f
        var mAdapter: BaseAdapter? = null

        fun setAutoPlay(enable: Boolean) : Build {
            isAutoPlay = enable
            return this
        }

        fun setTimeInterval(interval: Float) : Build {
            timeInterval = interval
            return this
        }

        fun setAdapter(adapter: BaseAdapter) : Build {
            mAdapter = adapter
            return this
        }
    }
}