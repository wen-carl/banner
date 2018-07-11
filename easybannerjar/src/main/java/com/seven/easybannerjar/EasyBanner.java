package com.seven.easybannerjar;

import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.BaseAdapter;
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
import android.widget.TextView;

import com.seven.easybannerjar.R.layout;
import com.seven.easybannerjar.model.DataModel;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static final int STYLE_CIRCLE_INDICATOR = 1;
    public static final int STYLE_TITLE_WITH_CIRCLE_INDICATOR_INSIDE = 2;
    public static final int STYLE_TITLE_WITH_CIRCLE_INDICATOR_OUTSIDE = 3;
    public static final int STYLE_NUM_INDICATOR = 10;
    public static final int STYLE_TITLE_WITH_NUM_INDICATOR_INSIDE = 11;
    public static final int STYLE_TITLE_WITH_NUM_INDICATOR_OUTSIDE = 12;
    public static final int STYLE_TITLE = 20;

    @IntDef({STYLE_NONE, STYLE_CIRCLE_INDICATOR, STYLE_TITLE_WITH_CIRCLE_INDICATOR_INSIDE, STYLE_TITLE_WITH_CIRCLE_INDICATOR_OUTSIDE, STYLE_NUM_INDICATOR, STYLE_TITLE_WITH_NUM_INDICATOR_INSIDE, STYLE_TITLE_WITH_NUM_INDICATOR_OUTSIDE, STYLE_TITLE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface IndicatorStyle {}

    private ViewPager mViewPager;
    private BaseAdapter mAdapter;
    private boolean isAutoPlay = true;
    private long mTimeInterval = 2000L;
    private @Mode int mMode = MODE_DEFAULT;
    private @BannerStatus int mStatus = STATUS_NOT_START;
    private @Direction int mDirection = DIRECTION_POSITIVE;
    private @IndicatorStyle int mIndicatorStyle = STYLE_CIRCLE_INDICATOR;

    private static final int BASE_INDICATOR_ID = 1000;
    private LinearLayout mCircleIndicator;
    private TextView mNumIndicator;
    private TextView mTitleIndicator;

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

    @IndicatorStyle
    public int getIndicatorStyle() {
        return mIndicatorStyle;
    }

    public EasyBanner setIndicatorStyle(@IndicatorStyle int style) {
        if (STATUS_NOT_START == mStatus) {
            mIndicatorStyle = style;
        } else {
            throw new IllegalArgumentException("Please call this method before the banner start!");
        }

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
        mCircleIndicator = indicatorView.findViewById(R.id.layout_image_indicator);
        mNumIndicator = indicatorView.findViewById(R.id.txt_num_indicator);
        mTitleIndicator = indicatorView.findViewById(R.id.txt_title);

        createCircleIndicator();
        mViewPager.setCurrentItem(1);
    }

    private void createCircleIndicator() {
        if (null == mCircleIndicator) {
            return;
        }

        mCircleIndicator.removeAllViews();
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int indicatorSize = dm.widthPixels / 80;

        for (int i = 0; i < mAdapter.getData().size(); i ++) {
            ImageView iv = new ImageView(getContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
            params.leftMargin = 5;
            params.rightMargin = 5;
            iv.setLayoutParams(params);
            iv.setImageResource(R.drawable.circle_indicator_background);
            iv.setSelected(i == 0);
            iv.setId(BASE_INDICATOR_ID + i);
            mCircleIndicator.addView(iv);
        }
    }

    private void updateIndicator(int position) {
        switch (mIndicatorStyle) {
            case STYLE_CIRCLE_INDICATOR:
            case STYLE_TITLE_WITH_CIRCLE_INDICATOR_INSIDE:
            case STYLE_TITLE_WITH_CIRCLE_INDICATOR_OUTSIDE:
                if (null != mCircleIndicator) {
                    ImageView cancel = mCircleIndicator.findViewById(BASE_INDICATOR_ID + mAdapter.getRealPosition(mCurrentIndex));

                    if (null != cancel) {
                        cancel.setSelected(false);
                    }

                    ImageView select = mCircleIndicator.findViewById(BASE_INDICATOR_ID + mAdapter.getRealPosition(position));
                    if (null != select) {
                        select.setSelected(true);
                    }
                }
                break;
            case STYLE_NUM_INDICATOR:
            case STYLE_TITLE_WITH_NUM_INDICATOR_INSIDE:
            case STYLE_TITLE_WITH_NUM_INDICATOR_OUTSIDE:
                if (null != mNumIndicator) {
                    mNumIndicator.setText(String.format("%d/%d", mAdapter.getRealPosition(position), mAdapter.getData().size()));
                }
                break;
            case STYLE_TITLE:
            case STYLE_NONE:
            default:
                break;
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

    public static abstract class BaseAdapter<T extends DataModel> extends PagerAdapter {

        private static final int DEFAULT_TYPE = -1;
        private static final int KEY_UNUSED = -1;

        private List<T> mData = new ArrayList<>();

        private List<T> mMockData;

        private HashMap<Number, HashMap<Number, View>> mCachedViewHolders = new HashMap<>();
        private HashMap<Number, View> mShowingViewHolders = new HashMap<>();

        private OnBannerItemClickListener mOnClickListener;
        private OnBannerItemLongClickListener mOnLongClickListener;

        public BaseAdapter(@NonNull List<T> mData) {
            bindData(mData);
        }

        @Override
        @NonNull
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            int type = getViewType(position);
            HashMap<Number, View> tempMap = mCachedViewHolders.get(position);

            View view = null;
            if (null != tempMap) {
                view = tempMap.get(KEY_UNUSED);
                tempMap.remove(KEY_UNUSED);
            }

            if (null == view) {
                view = onCreateView(container, getRealPosition(position), type);
            }

            container.addView(view);
            onDisplay(view, getRealPosition(position), mMockData.get(position));

            mShowingViewHolders.put(position, view);
            view.setTag(getRealPosition(position));
            setClickListener(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
            mShowingViewHolders.remove(position);

            int type = getViewType(position);
            HashMap<Number, View> tempMap = mCachedViewHolders.get(type);
            if (null == tempMap) {
                tempMap = new HashMap<>();
                mCachedViewHolders.put(KEY_UNUSED, tempMap);
            }
            tempMap.put(type, view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mMockData.size();
        }

        public int getViewType(int position) {
            return DEFAULT_TYPE;
        }

        @NonNull
        abstract public View onCreateView(@NonNull ViewGroup parent, int position, int viewType);

        abstract public void onDisplay(@NonNull View view, int position, @NonNull T model);

        public View onCreateIndicatorLayout(@NonNull ViewGroup parent, int position, int viewType) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_default_indicator_content, parent, false);
        }

        protected void onBindIndicator(@NonNull View view, int position, T model) {

        }

        public void setOnBannerItemClickListener(OnBannerItemClickListener listener) {
            mOnClickListener = listener;
        }

        public void setOnBannerItemLongClickListener(OnBannerItemLongClickListener listener) {
            mOnLongClickListener = listener;
        }

        private void setClickListener(final View view) {
            if (!view.hasOnClickListeners()) {
                if (null != mOnClickListener) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int position = (int)view.getTag();
                            mOnClickListener.onBannerClicked(v, position, mData.get(position));
                        }
                    });
                }
            }

            // TODO: 2018/7/9  Need to fix
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (null != mOnLongClickListener) {
                        int position = (int)view.getTag();
                        mOnClickListener.onBannerClicked(v, position, mData.get(position));

                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        protected int getRealPosition(int position) {
            int real = 0;
            if (mData.size() <= 1) {
                real = 0;
            } else {
                real = (position - 1) % mData.size();
                if (real < 0) {
                    real += mData.size();
                }
            }

            return real;
        }

        private void updateMockedData() {
            mMockData = new ArrayList<>(mData);
            if (mData.size() > 1) {
                T first = mData.get(0);
                T last = mData.get(mData.size() - 1);

                mMockData.add(0, last);
                mMockData.add(first);
            }

            notifyDataSetChanged();
        }

        public void bindData(@NonNull List<T> mData) {
            this.mData = mData;
            updateMockedData();
        }

        public List<T> getData() {
            return mData;
        }

    /*
    fun add(index: Int, model: T) {
        val temp = mData.toMutableList()
        temp.add(index, model)
        this.mData = temp.toList()
    }

    fun add(model: T) : Boolean {
        val temp = mData.toMutableList()
        val result = temp.add(model)
        if (result) {
            this.mData = temp.toList()
        }

        return result
    }

    fun add(index: Int, list: List<T>) : Boolean {
        val temp = mData.toMutableList()
        val result = temp.addAll(index, list)
        if (result) {
            this.mData = temp.toList()
        }

        return result
    }

    fun add(list: List<T>) : Boolean {
        val temp = mData.toMutableList()
        val result = temp.addAll(list)
        if (result) {
            this.mData = temp.toList()
        }

        return result
    }

    fun remove(index: Int) : T {
        val temp = mData.toMutableList()
        val result = temp.removeAt(index)
        this.mData = temp.toList()

        return result
    }

    fun remove(list: List<T>) : Boolean {
        val temp = mData.toMutableList()
        val result = temp.removeAll(list)
        if (result) {
            this.mData = temp.toList()
        }

        return result
    }

    fun clear() {
        val temp = mData.toMutableList()
        val result = temp.clear()
        this.mData = temp.toList()

        return result
    }
    */

    /*
    public static class ViewHolder {
        public int position = -1;
        public View itemView;

        public ViewHolder() {}

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
    */
    }

    public interface OnBannerItemClickListener {
        void onBannerClicked(@NonNull View view, int  position, @NonNull DataModel model);
    }

    public interface OnBannerItemLongClickListener {
        void onBannerLongClicked(View view, int  position, DataModel model);
    }
}


