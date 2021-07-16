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
    public static final String INVITE_TYPE = "inviteType";
    public static final int INVITE_STATUS_ACCEPTED = 1;
    public static final int INVITE_STATUS_REJECTED = 0;
    public static final int INVITE_STATUS_NONE = -1;
    public static final boolean GROUP_INVITE_CODE = true;
    public static final boolean FRIEND_INVITE_CODE = false;
    public static final String KEY_TO_JOIN_GROUP = "toJoinGroup";
    public static final String KEY_TO_FRIEND_USER = "toFriendUser";

    public static void newInvite(ParseUser user, Group group) {
        Invite invite = new Invite();
        invite.put(KEY_IS_GROUP_INVITE,true);
        invite.put(KEY_SENDER,User.currentUser);
        invite.put(KEY_RECIPIENT,user);
        invite.put(KEY_STATUS,Invite.INVITE_STATUS_NONE);
        invite.put(KEY_TO_JOIN_GROUP,group);
        invite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error saving invite: " + e.getMessage(),e);
                }
                Log.i(TAG,"Invite sent successfully.");
            }
        });
    }

    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }
    
    public ParseUser getRecipient() {
        return getParseUser(KEY_RECIPIENT);
    }
    
    public boolean getStatus() {
        return getBoolean(KEY_STATUS);
    }

    public void setSender(ParseUser sender) {
        put(KEY_SENDER, sender);
    }

    public void setRecipient(ParseUser recipient) {
        put(KEY_RECIPIENT, recipient);
    }

    public void setStatus(boolean status) {
        put(KEY_STATUS, status);
        User.updateInviteStatus(this);
    }
}
