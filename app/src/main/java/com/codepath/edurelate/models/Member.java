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

    public Member() {

    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
}
