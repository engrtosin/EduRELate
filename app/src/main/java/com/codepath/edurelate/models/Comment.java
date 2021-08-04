package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String TAG = "Comment";
}
