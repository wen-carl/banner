package com.seven.easybannerjar.model;

import android.support.annotation.IdRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DataModel {

    public static final int Net = 0;
    public static final int Disk = 1;
    public static final int Resource = 2;
    @IntDef({Net, Disk, Resource})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataType {}

    private String description;
    private int type;
    private String url;
    private @IdRes int id;

    public DataModel() {}

    public DataModel(String description, @IdRes int id) {
        this.type = Resource;
        this.description = description;
        this.id = id;
    }

    public DataModel(String description, @DataType int type, String url) {
        this.type = type;
        this.description = description;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }
}
