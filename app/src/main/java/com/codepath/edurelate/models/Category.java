package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Category.class)
@ParseClassName("Category")
public class Category extends ParseObject {

    public static final String TAG = "CategoryModel";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CODE = "code";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public int getCode() {
        return getInt(KEY_CODE);
    }
}
