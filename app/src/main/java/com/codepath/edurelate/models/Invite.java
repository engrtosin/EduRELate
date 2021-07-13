package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = Invite.class)
@ParseClassName("Invite")
public class Invite extends ParseObject {

    public static final String TAG = "InviteModel";
    public static final String KEY_SENDER = "inviteSender";
    public static final String KEY_RECIPIENT = "inviteRecipient";
    public static final String KEY_STATUS = "status";

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
