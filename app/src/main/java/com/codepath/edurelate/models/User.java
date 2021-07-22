package com.codepath.edurelate.models;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Queue;

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
    public static final String KEY_INVITE_SENT = "invitesSent";
    public static final String KEY_INVITE_RECEIVED = "invitesReceived";
    public static final String KEY_USER = "user";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String BOT_OBJECT_ID = "kJmCehSRZm";
    public static final String KEY_EMAIL = "email";

    public static ParseUser currentUser;
    public static ParseUser edurelateBot;
    public static Group parseGroup;

    public static String getFirstName(ParseUser user) {
        String firstName = user.getString(KEY_FIRST_NAME);
        return firstName;
    }

    public static String getLastName(ParseUser user) {
        String lastName = user.getString(KEY_LAST_NAME);
        return lastName;
    }

    public static String getFullName(ParseUser user) {
        String firstName = null;
        String lastName = null;
        try {
            firstName = user.fetchIfNeeded().getString(KEY_FIRST_NAME);
            lastName = user.fetchIfNeeded().getString(KEY_LAST_NAME);
        } catch (ParseException e) {
            Log.e(TAG,"Error while getting first and last names: " + e.getMessage(),e);
        }
        return firstName + " " + lastName;
    }

    public static List<Group> getAllGroups(ParseUser user) {
        return user.getList(KEY_GROUPS);
    }

    public static List<Group> getNonFriendGroups(ParseUser user) {
        Log.i(TAG,"User " + user.getUsername() + " has: " + user.has(KEY_NON_FRIEND_GROUPS));
        List<Group> groups = user.getList(KEY_NON_FRIEND_GROUPS);
        return groups;
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

    public static void addNewGroup(Group group,ParseUser user) throws ParseException {
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

    public static void acceptInvite(Invite invite) {
        if (invite.getIsGroupInvite()) {
            invite.setStatus(Invite.INVITE_STATUS_ACCEPTED);
            // add the person to the group
            Group group = invite.getToJoinGroup();
            ParseUser sender = invite.getSender();
            if (User.compareUsers(sender,group.getOwner())) {
                ParseUser recipient = invite.getRecipient();
                Member member = group.removeInvitee(recipient);
//                    group.addMember(member);
            }
            Member member = group.removeJoinRequester(sender);
//            group.addMember(member);
        }
    }

    public static void updateInviteStatus(Invite invite) {
        // add the recipient to the group or as a friend to sender
        // send a message to the recipient
        // send a message to the sender
    }

    public static boolean compareUsers(ParseUser user, ParseUser otherUser) {
        return user.getObjectId().equals(otherUser.getObjectId());
    }

    public static ParseUser findFriend(ParseUser user, Group group) {
        List<ParseUser> members = group.getMembers();
        for (ParseUser member : members) {
            if (!User.compareUsers(user,member)) {
                return member;
            }
        }
        return  null;
    }

    public static void addInviteReceived(Invite invite, ParseUser user) {
        user.add(KEY_INVITE_RECEIVED,invite);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG,"Error while adding invite received: " + e.getMessage(),e);
                }
                Log.i(TAG,"InviteReceived added successfully.");
            }
        });
    }

    public static void addInviteSent(Invite invite, ParseUser currentUser) {
        currentUser.add(KEY_INVITE_SENT,invite);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG,"Error while adding invite sent: " + e.getMessage(),e);
                }
                Log.i(TAG,"InviteSent added successfully.");
            }
        });
    }
}
