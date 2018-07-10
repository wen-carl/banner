package com.seven.easybannerjar;

import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seven.easybannerjar.adapter.BaseAdapter;
import com.seven.easybannerjar.R.layout;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public final class EasyBanner extends FrameLayout implements OnPageChangeListener {

    /**
     * Banner mStatus
     */
    public static final int STATUS_NOT_START = 0;
    public static final int STATUS_AUTO_PLAYING = 1;
    public static final int STATUS_MANUAL_LAYING = 2;
    public static final int STATUS_PAUSED = 3;
    public static final int STATUS_STOPED = 4;

    @IntDef({STATUS_NOT_START, STATUS_AUTO_PLAYING, STATUS_MANUAL_LAYING, STATUS_PAUSED, STATUS_STOPED})
    @Retention(RetentionPolicy.SOURCE)
    private @interface BannerStatus {}

    /**
     * Scroll mDirection
     */
    public static final int DIRECTION_POSITIVE = 0;
    public static final int DIRECTION_NEGATIVE = 1;

    @IntDef({DIRECTION_POSITIVE, DIRECTION_NEGATIVE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Direction {}

    /**
     * UI mode
     */
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_CUSTOM = 1;

    @IntDef({MODE_DEFAULT, MODE_CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Mode {}

    /**
     * Image banner style
     */
    public static final int STYLE_NONE = 0;
    public static final int STYLE_ONLY_CIRCLE_INDICATOR = 1;
    public static final int STYLE_TITLE_WITH_CIRCLE_INDICATOR_INSIDE = 2;
    public static final int STYLE_TITLE_WITH_CIRCLE_INDICATOR_OUTSIDE = 3;
    public static final int STYLE_ONLY_NUM_INDICATOR = 10;
    public static final int STYLE_TITLE_WITH_NUM_INDICATOR_INSIDE = 11;
    public static final int STYLE_TITLE_WITH_NUM_INDICATOR_OUTSIDE = 12;
    public static final int STYLE_ONLY_TITLE = 20;

    @IntDef({STYLE_NONE, STYLE_ONLY_CIRCLE_INDICATOR, STYLE_TITLE_WITH_CIRCLE_INDICATOR_INSIDE, STYLE_TITLE_WITH_CIRCLE_INDICATOR_OUTSIDE, STYLE_ONLY_NUM_INDICATOR, STYLE_TITLE_WITH_NUM_INDICATOR_INSIDE, STYLE_TITLE_WITH_NUM_INDICATOR_OUTSIDE, STYLE_ONLY_TITLE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface BannerStyle {}

    private ViewPager mViewPager;
    private BaseAdapter mAdapter;
    private boolean isAutoPlay = true;
    private long mTimeInterval = 2000L;
    private @Mode int mMode = MODE_DEFAULT;
    private @BannerStatus int mStatus = STATUS_NOT_START;
    private @Direction int mDirection = DIRECTION_POSITIVE;
    private @BannerStyle int mBannerStyle = STYLE_ONLY_CIRCLE_INDICATOR;

    private List<ImageView> mCircleIndicators;
    private int mCurrentIndex = 1;

    private static final Handler mHandler = new Handler();
    private final Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAdapter.getData().size() > 1 && isAutoPlay) {
                if (DIRECTION_POSITIVE == mDirection) {
                    showNext();
                } else {
                    showPrevious();
                }

                mHandler.postDelayed(mLoopRunnable, mTimeInterval);
            }
        }
    };

    public EasyBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EasyBanner(@NotNull Context context) {
        this(context, null);
    }

    public EasyBanner(@NotNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    @NotNull
    public EasyBanner setAdapter(@NotNull BaseAdapter adapter) {
        if (STATUS_NOT_START == mStatus) {
            mAdapter = adapter;
        } else if (STATUS_STOPED == mStatus || STATUS_PAUSED == mStatus){
            mAdapter = adapter;
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(1);
        } else {
            throw new IllegalStateException("Adapter can not be replaced when it is playing!");
        }

        switch (mStatus) {
            case STATUS_PAUSED:
            case STATUS_STOPED:
                mViewPager.setAdapter(adapter);
                mViewPager.setCurrentItem(1);
            case STATUS_NOT_START:
                mAdapter = adapter;
                break;
            case STATUS_AUTO_PLAYING:
            case STATUS_MANUAL_LAYING:
            default:
                throw new IllegalArgumentException("Adapter can not be replaced when it is playing!");
        }

        return this;
    }

    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    @NotNull
    public final EasyBanner setAutoPlay(boolean enable) {
        switch (mStatus) {
            case STATUS_AUTO_PLAYING:
                if (enable != isAutoPlay) {
                    if (enable) {
                        mHandler.postDelayed(mLoopRunnable, mTimeInterval);
                    } else {
                        mHandler.removeCallbacks(mLoopRunnable);
                    }

                    mStatus = enable ? STATUS_AUTO_PLAYING : STATUS_MANUAL_LAYING;
                }
            case STATUS_NOT_START:
                isAutoPlay = enable;
                break;
            case STATUS_PAUSED:
            case STATUS_STOPED:
            case STATUS_MANUAL_LAYING:
            default:
                break;
        }

        return this;
    }

    public long getTimeInterval() {
        return this.mTimeInterval;
    }

    @NotNull
    public final EasyBanner setTimeInterval(long interval) {
        mTimeInterval = interval;
        return this;
    }

    @Direction
    public int getDirection() {
        return mDirection;
    }

    public EasyBanner setDirection(@Direction int direction) {
        mDirection = direction;
        return this;
    }

    @Mode
    public int getMode() {
        return mMode;
    }

    public EasyBanner setMode(@Mode int mode) {
        if (STATUS_NOT_START == mStatus) {
            mMode = mode;
        } else {
            throw new IllegalArgumentException("Please call this method before the banner start!");
        }
        return this;
    }

    @BannerStyle
    public int getBannerStyle() {
        return mBannerStyle;
    }

    public EasyBanner setBannerStyle(@BannerStyle int style) {
        if (STATUS_NOT_START == mStatus) {
            mBannerStyle = style;
        } else {
            throw new IllegalArgumentException("Please call this method before the banner start!");
        }

        return this;
    }

    public EasyBanner setIndicatorPosition(int gravity) {


        return this;
    }

    public EasyBanner setTitlePosition(int gravity) {


        return this;
    }

    @BannerStatus
    public int getStatus() {
        return mStatus;
    }

    private void initView() {
        ViewGroup mainContent = (ViewGroup) LayoutInflater.from(getContext()).inflate(layout.layout_easy_banner_java, this, true);
        mViewPager = mainContent.findViewById(R.id.banner_view_pager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mAdapter);

        View indicatorView = mAdapter.onCreateIndicatorLayout(mainContent, 0, mAdapter.getViewType(0));
//        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.startToStart = mainContent.getId();
//        layoutParams.endToEnd = mainContent.getId();
//        layoutParams.bottomToBottom = mainContent.getId();
//        indicatorView.setLayoutParams(layoutParams);
        ConstraintLayout superLayout = mainContent.findViewById(R.id.mainContent);
        superLayout.addView(indicatorView);

        createCircleIndicator();
        mViewPager.setCurrentItem(1);
    }

    private void createCircleIndicator() {
        if (null == mCircleIndicators) {
            mCircleIndicators = new ArrayList<>();
        }

        mCircleIndicators.clear();
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int indicatorSize = dm.widthPixels / 80;
        LinearLayout indicatorLayout = findViewById(R.id.layout_circle_indicator);

        for (int i = 0; i < mAdapter.getData().size(); i ++) {
            ImageView iv = new ImageView(getContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
            params.leftMargin = 5;
            params.rightMargin = 5;
            iv.setLayoutParams(params);
            iv.setImageResource(R.drawable.circle_indicator_background);
            iv.setSelected(i == 0);
            mCircleIndicators.add(iv);
            indicatorLayout.addView(iv);
        }
    }

    public final void start() {
        boolean shouldStart = isAutoPlay;
        switch(mStatus) {
            case STATUS_NOT_START:
                initView();
                mStatus = isAutoPlay ? STATUS_AUTO_PLAYING : STATUS_MANUAL_LAYING;
                break;
            case STATUS_AUTO_PLAYING:
            case STATUS_PAUSED:
            case STATUS_STOPED:
                shouldStart &= true;
                break;
            case STATUS_MANUAL_LAYING:
            default:
                shouldStart = false;
        }

        if (shouldStart) {
            mHandler.removeCallbacks(mLoopRunnable);
            mHandler.postDelayed(mLoopRunnable, mTimeInterval);
        }
    }

    public final void stop() {
        this.mStatus = STATUS_STOPED;
        mHandler.removeCallbacks(mLoopRunnable);
        
        if (mCurrentIndex != 1) {
            int position;
            switch (this.mDirection) {
                case DIRECTION_POSITIVE:
                    position = 0;
                    break;
                case DIRECTION_NEGATIVE:
                default:
                    position = 2;
            }

            this.mViewPager.setCurrentItem(position, false);
            this.mViewPager.setCurrentItem(1);
        }
    }

    public void pause() {
        this.mStatus = STATUS_PAUSED;
        mHandler.removeCallbacks(mLoopRunnable);
    }

    public void showPrevious() {
        int index = mCurrentIndex == 0 ? mAdapter.getData().size() : mCurrentIndex - 1;
        this.show(index);
    }

    public void showNext() {
        int index = mCurrentIndex == mAdapter.getData().size() + 1 ? 1 : mCurrentIndex + 1;
        this.show(index);
    }

    public void show(int index) {
        int temp = index <= 0 ? 0 : (index <= mAdapter.getData().size() + 1 ? index : mAdapter.getData().size() + 1);
        this.mViewPager.setCurrentItem(temp);
    }

    public void onPageScrollStateChanged(int state) {
        switch(state) {
            case 0:
            case 1:
                if (mCurrentIndex == 0) {
                    mViewPager.setCurrentItem(mAdapter.getData().size(), false);
                } else if (mCurrentIndex == mAdapter.getData().size() + 1) {
                    mViewPager.setCurrentItem(1, false);
                }
            case 2:
            default:
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
        updateIndicator(position);
        mCurrentIndex = position;
    }

    private void updateIndicator(int position) {
        mCircleIndicators.get(getRealPosition(mCurrentIndex)).setSelected(false);
        mCircleIndicators.get(getRealPosition(position)).setSelected(true);
    }

    private int getRealPosition(int position) {
        int real = 0;
        int count = mAdapter.getData().size();
        if (count <= 1) {
            real = 0;
        } else {
            real = (position - 1) % count;
            if (real < 0) {
                real += count;
            }
        }

        return real;
    }

    /**
     * 事件分发，手指按在banner上时停止自动滚动
     * @param ev
     * @return
     */
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE
                ) {
            if (isAutoPlay && mStatus == STATUS_AUTO_PLAYING) {
                mHandler.postDelayed(mLoopRunnable, mTimeInterval);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            mHandler.removeCallbacks(mLoopRunnable);
        }

        return super.dispatchTouchEvent(ev);
    }
}


