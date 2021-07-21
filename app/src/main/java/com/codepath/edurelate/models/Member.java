package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = Member.class)
@ParseClassName("Member")
public class Member extends ParseObject {

    public static final String TAG = "MemberModel";
    public static final String KEY_USER = "user";
    public static final String KEY_INVITE = "invite";
    public static final String KEY_GROUP = "group";

    public Member() {

    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public Invite getInvite() {
        return (Invite) getParseObject(KEY_INVITE);
    }
}
