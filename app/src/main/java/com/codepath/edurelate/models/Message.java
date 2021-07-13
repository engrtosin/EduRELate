package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

import java.util.Collections;
import java.util.List;

@Parcel(analyze = Message.class)
@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String TAG = "MessageModel";
    public static final String KEY_BODY = "body";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_RECIPIENT = "recipient";
    public static final String KEY_REPLY_TO = "replyTo";
    public static final String KEY_USERS_LIKING_THIS = "usersLikingThis";
    public static final int TRUNCATED_BODY_LENGTH = 50;

    public String getBody(boolean isFull) {
        String body = getString(KEY_BODY);
        if (!isFull) {
            return body.substring(0,TRUNCATED_BODY_LENGTH) + "...";
        }
        return body;
    }
    
    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }

    public ParseUser getRecipient() {
        return getParseUser(KEY_RECIPIENT);
    }

    public Message getReplyTo() {
        return (Message) getParseObject(KEY_REPLY_TO);
    }

    public List<ParseUser> getUsersLikingThis() {
        return getList(KEY_USERS_LIKING_THIS);
    }

    public int getLikeCount() {
        return getUsersLikingThis().size();
    }

    public void unlikeThis(ParseUser user) {
        removeAll(KEY_USERS_LIKING_THIS, Collections.singleton(user));
    }

    public void likeThis(ParseUser user) {
        add(KEY_USERS_LIKING_THIS,user);
    }
}