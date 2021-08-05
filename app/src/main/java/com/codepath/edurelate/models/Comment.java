package com.codepath.edurelate.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Parcel(analyze = Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String TAG = "Comment";
    public static final String KEY_COMMENT_OWNER = "commentOwner";
    public static final String KEY_TO_POST = "toPost";
    private static final String KEY_COMMENT_BODY = "commentBody";
    private static final String KEY_VOTE_COUNT = "voteCount";
    public static final String KEY_USERS_UPVOTING = "usersUpvotingThis";
    public static final String KEY_USERS_DOWNVOTING = "usersDownvotingThis";
    private static final int SHORTENED_BODY_LENGTH = 60;

    public static Comment newComment(Post post, String text) {
        Comment comment = new Comment();
        comment.put(KEY_COMMENT_BODY,text);
        comment.put(KEY_TO_POST,post);
        comment.put(KEY_VOTE_COUNT,0);
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

    public int getVoteCount() {
        return getInt(KEY_VOTE_COUNT);
    }

    public String getVoteCountString() {
        return "" + getInt(KEY_VOTE_COUNT);
    }

    public ParseUser getCommentOwner() {
        return getParseUser(KEY_COMMENT_OWNER);
    }

    public List<String> getUsersUpvoting() {
        return getList(KEY_USERS_UPVOTING);
    }

    public List<String> getUsersDownvoting() {
        return getList(KEY_USERS_DOWNVOTING);
    }

    public void upvote() {
        String currUserId = ParseUser.getCurrentUser().getObjectId();
        if (getUsersUpvoting().contains(currUserId)) {
            return;
        }
        int count = getVoteCount() + 1;
        if (getUsersDownvoting().contains(currUserId)) {
            removeAll(KEY_USERS_DOWNVOTING, Collections.singletonList(currUserId));
            count += 1;
        }
        put(KEY_VOTE_COUNT,count);
        add(KEY_USERS_UPVOTING,currUserId);
        saveInBackground();
    }

    public void downvote() {
        String currUserId = ParseUser.getCurrentUser().getObjectId();
        if (getUsersDownvoting().contains(currUserId)) {
            return;
        }
        int count = getVoteCount() - 1;
        if (getUsersUpvoting().contains(currUserId)) {
            removeAll(KEY_USERS_UPVOTING, Collections.singletonList(currUserId));
            count -= 1;
        }
        Log.i(TAG,"COUNT: " + count);
        put(KEY_VOTE_COUNT,count);
        add(KEY_USERS_DOWNVOTING,currUserId);
        saveInBackground();
    }
}
