package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String TAG = "Comment";
    public static final String KEY_COMMENT_OWNER = "commentOwner";
    public static final String KEY_TO_POST = "toPost";
    private static final String KEY_COMMENT_BODY = "commentBody";
    private static final int SHORTENED_BODY_LENGTH = 60;

    public String getBody(boolean isFull) {
        String body = getString(KEY_COMMENT_BODY);
        if (!isFull && body.length() > SHORTENED_BODY_LENGTH) {
            body = body.substring(0,SHORTENED_BODY_LENGTH) + " ...";
        }
        return body;
    }
}
