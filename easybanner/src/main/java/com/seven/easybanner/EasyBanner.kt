package com.seven.easybanner

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import com.seven.easybanner.adapter.BaseAdapter
import android.widget.FrameLayout

class EasyBanner(context: Context) : FrameLayout(context), ViewPager.OnPageChangeListener {

    private var baseAdapter: BaseAdapter? = null

    /**
     * android.support.v4.view.ViewPager.OnPageChangeListener}
     */
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    class Adapter(val adapter: BaseAdapter) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return adapter.onCreatView(adapter.context, position) as Any
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            //super.destroyItem(container, position, `object`)
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return adapter.data.size
        }
    }
}