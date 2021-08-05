package com.codepath.edurelate.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

@Parcel(analyze = Post.class)
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String TAG = "Post";
    public static final String KEY_POST_TITLE = "postTitle";
    public static final String KEY_POST_BODY = "postBody";
    public static final String KEY_POST_OWNER = "postOwner";
    public static final String KEY_GROUP = "group";
    public static final String KEY_TOP_RATED_COMMENT = "topRatedComment";
    public static final int SHORTENED_BODY_LENGTH = 200;

    public static Post newPost(Group group, String topic, String body) {
        Post post = new Post();
        post.put(KEY_POST_TITLE,topic);
        post.put(KEY_POST_BODY,body);
        post.put(KEY_POST_OWNER, ParseUser.getCurrentUser());
        post.put(KEY_GROUP,group);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while saving post: " + e.getMessage(),e);
                    return;
                }
            }
        });
        return post;
    }

    public String getTitle() {
        return getString(KEY_POST_TITLE);
    }

    public String getBody(boolean isFull) {
        String body = getString(KEY_POST_BODY);
        if (!isFull && body.length() > SHORTENED_BODY_LENGTH) {
            body = body.substring(0,SHORTENED_BODY_LENGTH) + " ...";
        }
        return body;
    }

    public Comment getTopRatedComment() {
        return (Comment) getParseObject(KEY_TOP_RATED_COMMENT);
    }

    public ParseUser getPostOwner() {
        return getParseUser(KEY_POST_OWNER);
    }

    public void setTopRatedComment(Comment comment) {
        if (getTopRatedComment() != null) {
            return;
        }
        put(KEY_TOP_RATED_COMMENT,comment);
    }
}
