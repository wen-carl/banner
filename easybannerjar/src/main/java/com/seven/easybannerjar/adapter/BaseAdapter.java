package com.seven.easybannerjar.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seven.easybannerjar.R;
import com.seven.easybannerjar.model.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseAdapter<T extends DataModel> extends PagerAdapter {

    private static final int DEFAULT_TYPE = -1;
    private static final int KEY_UNUSED = -1;

    private List<T> mData = new ArrayList<>();

    private List<T> mMockData;

    private HashMap<Number, HashMap<Number, View>> mCachedViewHolders = new HashMap<>();
    private HashMap<Number, View> mShowingViewHolders = new HashMap<>();

    private OnBannerClickListener mOnClickListener;
    private OnBannerLongClickListener mOnLongClickListener;
    
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

    public void onBindIndicator(@NonNull View view, int position, T model) {

    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnBannerLongClickListener(OnBannerLongClickListener listener) {
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

    private int getRealPosition(int position) {
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

    public interface OnBannerClickListener {
        void onBannerClicked(@NonNull View view, int  position, @NonNull DataModel model);
    }

    public interface OnBannerLongClickListener {
        void onBannerLongClicked(View view, int  position, DataModel model);
    }
}
