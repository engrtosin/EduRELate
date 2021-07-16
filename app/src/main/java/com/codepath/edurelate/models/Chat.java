package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Chat.class)
@ParseClassName("Chat")
public class Chat extends ParseObject {

    public static final String TAG = "ChatModel";
    public static final String KEY_ISGROUPCHAT = "isGroupChat";
    public static final String KEY_MESSAGES = "messages";
    public static final String KEY_CHAT = "chat";

    public void addMessage(Message message) {
    }

    public void addReply(Message message) {
    }

}
