package com.codepath.edurelate.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

@Parcel(analyze = Invite.class)
@ParseClassName("Invite")
public class Invite extends ParseObject {

    public static final String TAG = "InviteModel";
    public static final String KEY_SENDER = "inviteSender";
    public static final String KEY_RECIPIENT = "inviteRecipient";
    public static final String KEY_STATUS = "status";
    public static final String KEY_IS_GROUP_INVITE = "isGroupInvite";
    public static final String KEY_TO_JOIN_GROUP = "toJoinGroup";
    public static final String KEY_TO_FRIEND_USER = "toFriendUser";
    public static final String KEY_NEW_MEMBER = "newMember";
    public static final String INVITE_TYPE = "inviteType";
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_REJECTED = 0;
    public static final int STATUS_NONE = -1;
    public static final int GROUP_INVITE_CODE = 300;
    public static final int FRIEND_INVITE_CODE = 200;

    public static Invite newGroupInvite(ParseUser user, Group group) {
        Invite invite = new Invite();
        invite.put(KEY_IS_GROUP_INVITE,true);
        invite.put(KEY_SENDER,ParseUser.getCurrentUser());
        invite.put(KEY_NEW_MEMBER,user);
        invite.put(KEY_STATUS,Invite.STATUS_NONE);
        invite.put(KEY_TO_JOIN_GROUP,group);
        invite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error saving invite: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Invite sent successfully.");
            }
        });
        return invite;
    }

    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }
    
    public ParseUser getRecipient() {
        return getParseUser(KEY_RECIPIENT);
    }
    
    public int getStatus() {
        return getInt(KEY_STATUS);
    }

    public boolean getIsGroupInvite() {
        return getBoolean(KEY_IS_GROUP_INVITE);
    }

    public void setSender(ParseUser sender) {
        put(KEY_SENDER, sender);
    }

    public void setRecipient(ParseUser recipient) {
        put(KEY_RECIPIENT, recipient);
    }

    public void setStatus(int status) {
        put(KEY_STATUS, status);
    }

    public Group getToJoinGroup() {
        return (Group) getParseObject(KEY_TO_JOIN_GROUP);
    }

    public void acceptInvite() {
        put(KEY_STATUS, Invite.STATUS_ACCEPTED);
        // add new member to group
        // send notification to sender
        // send notification to recipient
    }

    public void rejectInvite() {
        put(KEY_STATUS, Invite.STATUS_ACCEPTED);
        // add new member to group
        // send notification to sender
        // send notification to recipient
    }
}
