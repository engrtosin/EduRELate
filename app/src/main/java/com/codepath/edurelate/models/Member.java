package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Member.class)
@ParseClassName("Member")
public class Member extends ParseObject {

    public static final String TAG = "MemberModel";

}
