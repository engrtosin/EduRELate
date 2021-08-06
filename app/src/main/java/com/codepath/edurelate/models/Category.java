package com.codepath.edurelate.models;

import com.codepath.edurelate.R;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Category.class)
@ParseClassName("Category")
public class Category extends ParseObject {

    public static final String TAG = "CategoryModel";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CODE = "code";
    public static final int[] category_colors = new int[]{
            R.color.category_1,R.color.category_2,R.color.category_3,
            R.color.category_4,R.color.category_5,R.color.category_6,
            R.color.category_7,R.color.category_8,R.color.category_9,
            R.color.category_10,R.color.category_11,R.color.category_12,
            R.color.category_13,R.color.category_14,R.color.category_15,
            R.color.category_16,R.color.category_17,R.color.category_18,
            R.color.category_19
    };

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public int getCode() {
        return getInt(KEY_CODE);
    }
}
