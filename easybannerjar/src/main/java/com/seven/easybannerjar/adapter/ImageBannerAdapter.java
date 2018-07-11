package com.seven.easybannerjar.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seven.easybannerjar.R;
import com.seven.easybannerjar.model.DataModel;
import com.seven.easybannerjar.EasyBanner.BaseAdapter;

import java.util.List;

public final class ImageBannerAdapter<T extends DataModel> extends BaseAdapter<T> {

    private IImageLoader mLoader;

    public ImageBannerAdapter(@NonNull List<T> mData, IImageLoader loader) {
        super(mData);
        mLoader = loader;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup parent, int position, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
    }

    @Override
    public void onDisplay(@NonNull View view, int position, @NonNull T model) {
        if (null != mLoader) {
            ImageView iv = (ImageView) view;
            mLoader.load(iv, position, model);
        }
    }

    public interface IImageLoader<D extends DataModel> {
        void load(@NonNull ImageView imageView, int position, @NonNull D model);
    }
}
