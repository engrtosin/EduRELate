package com.codepath.edurelate.models;

import android.util.Log;

import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.MessagesAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    public static final String KEY_INVITE_SENT = "invitesSent";
    public static final String KEY_INVITE_RECEIVED = "invitesReceived";
    public static final String KEY_USER = "user";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String BOT_OBJECT_ID = "kJmCehSRZm";
    public static final String KEY_EMAIL = "email";

    public static ParseUser currentUser;
    public static ParseUser edurelateBot;
    public static Group parseGroup;
    public static List<Group> currUserGroups = new ArrayList<>();

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
                    return;
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
                    return;
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
            invite.setStatus(Invite.STATUS_ACCEPTED);
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

    public static void addInviteReceived(Invite invite, ParseUser user) {
        user.add(KEY_INVITE_RECEIVED,invite);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG,"Error while adding invite received: " + e.getMessage(),e);
                    return;
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
                    return;
                }
                Log.i(TAG,"InviteSent added successfully.");
            }
        });
    }

    public static void clearCurrUserData() {
        currUserGroups = null;
    }

    public static List<String> getCurrGroupIds() {
        List<String> groupIds = new ArrayList<>();
        for (int i = 0; i < currUserGroups.size(); i++) {
            groupIds.add(currUserGroups.get(i).getObjectId());
        }
        return groupIds;
    }

    public static void sendGroupRequest(Group group) {
        ParseUser currUser = ParseUser.getCurrentUser();
        Request request = Request.newInstance(currUser,group);
        String txtToOwner = User.getFullName(currUser) + " wants to join your group: " + group.getGroupName();
        Notification toOwner = Notification.newInstance(group.getOwner(),Notification.REQUEST_RECEIVED_CODE,txtToOwner,request);
        String txtToUser = "You sent a request to join " + group.getGroupName();
        Notification toUser = Notification.newInstance(currUser,Notification.REQUEST_SENT_CODE,txtToUser,request);
    }

    public static void leaveGroup(Group group) {
        ParseUser currUser = ParseUser.getCurrentUser();
        ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
        query.whereEqualTo(Member.KEY_USER,currUser);
        query.whereContainedIn(Member.KEY_GROUP, Arrays.asList(group));
        query.findInBackground(new FindCallback<Member>() {
            @Override
            public void done(List<Member> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while getting corresponding member object: " + e.getMessage(),e);
                    return;
                }
                if (objects.size() > 0) {
                    Log.i(TAG,"Corresponding member for curr user and group (" + group.getObjectId() + ") found.");
                    objects.get(0).deleteInBackground();
                    deleteGroupForCurrUser(group);
                }
            }
        });
        String txtToOwner = User.getFullName(currUser) + " left your group: " + group.getGroupName();
        Notification toOwner = Notification.newInstance(group.getOwner(),Notification.MEMBER_LEFT_CODE,txtToOwner);
        String txtToUser = "You left " + group.getGroupName();
        Notification toUser = Notification.newInstance(currUser,Notification.YOU_LEFT_GROUP_CODE,txtToUser);
    }

    private static void deleteGroupForCurrUser(Group group) {
        if (currUserGroups.contains(group)) {
            currUserGroups.remove(group);
            return;
        }
        for (int i = 0; i < currUserGroups.size(); i++) {
            Log.i(TAG,"is group same as this in currUserGroups: " + i + ", " + currUserGroups.get(i).getObjectId().equals(group.getObjectId()));
            if (currUserGroups.get(i).getObjectId().equals(group.getObjectId())) {
                currUserGroups.remove(currUserGroups.get(i));
                return;
            }
        }
    }
}
