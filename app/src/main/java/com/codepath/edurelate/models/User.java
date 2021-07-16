package com.codepath.edurelate.models;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class User {

    public static final String TAG = "UserModel";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_USER_PIC = "userPic";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_GROUPS = "groups";
    public static final String KEY_NON_FRIEND_GROUPS = "nonFriendGroups";
    public static final String KEY_MAJOR = "major";
    public static final String KEY_USER = "user";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String BOT_OBJECT_ID = "kJmCehSRZm";

    public static ParseUser currentUser;
    public static ParseUser edurelateBot;

    public static String getFirstName(ParseUser user) throws ParseException {
        String firstName = user.fetchIfNeeded().getString(KEY_FIRST_NAME);
        return firstName;
    }

    public static String getLastName(ParseUser user) throws ParseException {
        String lastName = user.fetchIfNeeded().getString(KEY_LAST_NAME);
        return lastName;
    }

    public static String getFullName(ParseUser user) throws ParseException {
        String firstName = user.fetchIfNeeded().getString(KEY_FIRST_NAME);
        String lastName = user.fetchIfNeeded().getString(KEY_LAST_NAME);
        return firstName + " " + lastName;
    }

    public static List<Group> getAllGroups(ParseUser user) {
        return user.getList(KEY_GROUPS);
    }

    public static List<Group> getNonFriendGroups(ParseUser user) {
        Log.i(TAG,"User " + user.getUsername() + " has: " + user.has(KEY_NON_FRIEND_GROUPS));
        return user.getList(KEY_NON_FRIEND_GROUPS);
    }

    public static void setFirstName(ParseUser user, String firstName) {
        user.put(KEY_FIRST_NAME,firstName);
    }

    public static void setLastName(ParseUser user, String lastName) {
        user.put(KEY_LAST_NAME,lastName);
    }

    public static void setUserPic(ParseUser user, ParseFile parseFile) {
        user.put(KEY_USER_PIC,parseFile);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error updating user " + user.getObjectId() + " pic");
                }
            }
        });
    }

    public static void addNewGroup(Group group,ParseUser user) {
        user.add(KEY_GROUPS,group);
        if (!group.getIsFriendGroup()) {
            user.add(KEY_NON_FRIEND_GROUPS,group);
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error adding group to current user: " + e.getMessage(),e);
                }
                Log.i(TAG,"group added to current user.");
            }
        });
    }

    public static Message sendMessage(String body, ParseUser sender, Group recipient, Message replyTo) throws ParseException {
        Message message = new Message();
        message.put(Message.KEY_BODY, body);
        message.put(Message.KEY_SENDER, sender);
        message.put(Message.KEY_REPLY_TO, replyTo);
        message.put(Message.KEY_RECIPIENT, recipient);
        message.save();
        recipient.getChat().addMessage(message);
        return message;
    }

    public static void updateInviteStatus(Invite invite) {
        // add the recipient to the group or as a friend to sender
        // send a message to the recipient
        // send a message to the sender
    }

    public static boolean compareUsers(ParseUser user, ParseUser otherUser) {
        return user.getObjectId().equals(otherUser.getObjectId());
    }
}
