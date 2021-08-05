package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String TAG = "Comment";
    public static final String KEY_COMMENT_OWNER = "commentOwner";
    public static final String KEY_TO_POST = "toPost";
    private static final String KEY_COMMENT_BODY = "commentBody";
    private static final String KEY_UPVOTE_COUNT = "upvoteCount";
    private static final String KEY_DOWNVOTE_COUNT = "downvoteCount";
    private static final String KEY_NETVOTE_COUNT = "netvoteCount";
    private static final int SHORTENED_BODY_LENGTH = 60;

    public static Comment newComment(Post post, String text) {
        Comment comment = new Comment();
        comment.put(KEY_COMMENT_BODY,text);
        comment.put(KEY_TO_POST,post);
        comment.put(KEY_UPVOTE_COUNT,0);
        comment.put(KEY_DOWNVOTE_COUNT,0);
        comment.put(KEY_NETVOTE_COUNT,0);
        comment.put(KEY_COMMENT_OWNER, ParseUser.getCurrentUser());
        comment.saveInBackground();
        return comment;
    }

    public String getBody(boolean isFull) {
        String body = getString(KEY_COMMENT_BODY);
        if (!isFull && body.length() > SHORTENED_BODY_LENGTH) {
            body = body.substring(0,SHORTENED_BODY_LENGTH) + " ...";
        }
        return body;
    }
}
