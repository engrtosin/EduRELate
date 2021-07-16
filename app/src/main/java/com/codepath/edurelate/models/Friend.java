package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Friend.class)
@ParseClassName("Friend")
public class Friend extends ParseObject {

    public static final String TAG = "FriendModel";
    public static final String KEY_CHAT = "chat";
    public static final String KEY_FRIEND = "friend";

    public Chat getChat() {
        return (Chat) getParseObject(KEY_CHAT);
    }
}
