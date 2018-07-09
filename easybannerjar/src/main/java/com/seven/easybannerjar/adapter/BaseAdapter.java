package com.seven.easybannerjar.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.seven.easybannerjar.model.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseAdapter<T extends DataModel, H extends BaseAdapter.ViewHolder> extends PagerAdapter {

    private static final int DEFAULT_TYPE = -1;
    private static final int KEY_UNUSED = -1;

    private List<T> data = new ArrayList<>();

    private List<T> mMockData;

    private HashMap<Number, HashMap<Number, ViewHolder>> mCachedViewHolders = new HashMap<>();
    private HashMap<Number, H> mShowingViewHolders = new HashMap<>();

    private OnBannerClickListener mOnClickListener;
    private OnBannerLongClickListener mOnLongClickListener;

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        int type = getViewType(position);
        HashMap<Number, ViewHolder> tempMap = mCachedViewHolders.get(position);

        H holder = null;
        if (null != tempMap) {
            holder = (H) tempMap.get(KEY_UNUSED);
            tempMap.remove(KEY_UNUSED);
        }

        if (null == holder) {
            holder = onCreateView(container, getRealPosition(position), type);
        }

        container.addView(holder.itemView);
        onDisplay(holder, getRealPosition(position), mMockData.get(position));

        mShowingViewHolders.put(position, holder);

        setClickListener(holder);

        return holder;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewHolder holder = (ViewHolder) object;
        container.removeView(holder.itemView);
        mShowingViewHolders.remove(position);

        int type = getViewType(position);
        HashMap<Number, ViewHolder> tempMap = mCachedViewHolders.get(type);
        if (null == tempMap) {
            tempMap = new HashMap<>();
            mCachedViewHolders.put(KEY_UNUSED, tempMap);
        }
        tempMap.put(type, holder);
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

    abstract public H onCreateView(ViewGroup parent, int position, int viewType);

    abstract public void onDisplay(H holder, int position, T model);

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnBannerLongClickListener(OnBannerLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    private void setClickListener(final H holder) {
        if (!holder.itemView.hasOnClickListeners()) {
            if (null != mOnClickListener) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener.onBannerClicked(v, holder.position, mMockData.get(holder.position));
                    }
                });
            }

            // TODO: 2018/7/9  Need to fix
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (null != mOnLongClickListener) {
                        mOnClickListener.onBannerClicked(v, holder.position, mMockData.get(holder.position));

                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    private int getRealPosition(int position) {
        int real = 0;
        if (data.size() <= 1) {
            real = 0;
        } else {
            real = (position - 1) % data.size();
            if (real < 0) {
                real += data.size();
            }
        }

        return real;
    }

    private void updateMockedData() {
        mMockData = new ArrayList<>(data);
        if (data.size() > 1) {
            T first = data.get(0);
            T last = data.get(data.size() - 1);

            mMockData.add(0, last);
            mMockData.add(first);
        }

        notifyDataSetChanged();
    }

    public void bindData(@NonNull List<T> data) {
        this.data = new ArrayList<>(data);
        updateMockedData();
    }

    public List<T> getData() {
        return data;
    }

    /*
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
    */

    public static class ViewHolder {
        public int position = -1;
        public View itemView;

        public ViewHolder() {}

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }

    public interface OnBannerClickListener {
        void onBannerClicked(View view, int  position, DataModel model);
    }

    public interface OnBannerLongClickListener {
        void onBannerLongClicked(View view, int  position, DataModel model);
    }
}
