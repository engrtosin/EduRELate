package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Friend.class)
@ParseClassName("Friend")
public class Friend extends ParseObject {

    public static final String TAG = "FriendModel";
}
