package com.seven.easybannerjar;

import android.os.Handler;
import android.widget.FrameLayout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.seven.easybannerjar.adapter.BaseAdapter;
import com.seven.easybannerjar.R.id;
import com.seven.easybannerjar.R.layout;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.jvm.internal.Intrinsics;

public class EasyBanner extends FrameLayout implements OnPageChangeListener {
    private ViewPager mViewPager;
    private boolean mTouching = false;
    private boolean isAutoPlay = true;
    private long timeInterval = 2000L;
    private BannerStatus status = BannerStatus.NotStart;
    private Direction direction = Direction.Positive;
    private int count = getCount();

    public EasyBanner(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isAutoPlay = true;
        this.timeInterval = 2000L;
        this.status = BannerStatus.NotStart;
        this.direction = Direction.Positive;
        View mainContent = LayoutInflater.from(context).inflate(layout.layout_easy_banner, this, true);
        this.mViewPager = mainContent.findViewById(id.banner_content);
        this.mViewPager.addOnPageChangeListener(this);
    }

    public EasyBanner(@NotNull Context context) {
        this(context, null);
    }

    public EasyBanner(@NotNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public boolean isAutoPlay() {
        return this.isAutoPlay;
    }

    public long getTimeInterval() {
        return this.timeInterval;
    }

    @NotNull
    public BannerStatus getStatus() {
        return this.status;
    }

    private void setStatus(BannerStatus var1) {
        this.status = var1;
    }

    @NotNull
    public Direction getDirection() {
        return this.direction;
    }

    public EasyBanner setDirection(@NotNull Direction direction) {
        this.direction = direction;

        return this;
    }

    private int getCount() {
        int count = 0;
        if (null != mViewPager) {
            BaseAdapter adapter = (BaseAdapter) mViewPager.getAdapter();
            if (null != adapter) {
                count = adapter.getData().size();
            }
        }

        return count;
    }

    @NotNull
    public final EasyBanner setAutoPlay(boolean enable) {
        if (enable && this.status != BannerStatus.AutoPlaying) {
            this.pause();
            this.start();
        }

        this.status = enable ? BannerStatus.AutoPlaying : BannerStatus.ManualPlaying;
        this.isAutoPlay = enable;

        return this;
    }

    @NotNull
    public final EasyBanner setTimeInterval(long interval) {
        if (interval != this.timeInterval) {
            this.timeInterval = interval;
            this.start();
        }

        return this;
    }

    @NotNull
    public final EasyBanner setAdapter(@NotNull BaseAdapter adapter) {
        this.mViewPager.setAdapter(adapter);
        this.mViewPager.setCurrentItem(1);

        return this;
    }

    private static Handler mHandler = new Handler();
    private Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    public final void start() {
        boolean shouldStart = true;
        switch(this.status) {
            case NotStart:
            case Paused:
            case Stoped:
                shouldStart = true;
                break;
            default:
                shouldStart = false;
        }

        if (this.isAutoPlay && shouldStart) {
            ;
        }
    }

    public final void stop() {
        this.pause();
        this.status = BannerStatus.Stoped;
        int position;
        switch(this.direction) {
            case Positive:
                position = 0;
                break;
            case Negative:
            default:
                position = 2;
        }

        this.mViewPager.setCurrentItem(position, false);
        this.mViewPager.setCurrentItem(1);
    }

    public void pause() {
        this.status = BannerStatus.Paused;
    }

    public void showPrevious() {
        int index = this.mViewPager.getCurrentItem() == 0 ? this.getCount() : this.mViewPager.getCurrentItem() - 1;
        this.show(index);
    }

    public void showNext() {
        int index = this.mViewPager.getCurrentItem() == this.getCount() + 1 ? 1 : this.mViewPager.getCurrentItem() + 1;
        this.show(index);
    }

    public void show(int index) {
        int temp = index <= 0 ? 0 : (index <= this.getCount() + 1 ? index : this.getCount() + 1);
        this.mViewPager.setCurrentItem(temp);
    }

    public void onPageScrollStateChanged(int state) {
        switch(state) {
            case 0:
            case 1:
                if (this.mViewPager.getCurrentItem() == 0) {
                    this.mViewPager.setCurrentItem(this.getCount(), false);
                } else if (this.mViewPager.getCurrentItem() == this.getCount() + 1) {
                    this.mViewPager.setCurrentItem(1, false);
                }
            case 2:
            default:
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
    }

    /**
     * 事件分发，手指按在banner上时停止自动滚动
     * @param ev
     * @return
     */
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        int action = ev.getAction();
        if (action != 1 && action != 3 && action != 4) {
            if (action == 0) {
                this.mTouching = true;
            }
        } else {
            this.mTouching = false;
        }

        return super.dispatchTouchEvent(ev);
    }

    public enum BannerStatus {
        NotStart,
        AutoPlaying,
        ManualPlaying,
        Paused,
        Stoped
    }

    public enum Direction {
        Positive,
        Negative;
    }
}


