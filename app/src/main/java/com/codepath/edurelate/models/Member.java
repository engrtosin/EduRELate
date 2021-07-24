package com.codepath.edurelate.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = Member.class)
@ParseClassName("Member")
public class Member extends ParseObject {

    public static final String TAG = "MemberModel";
    public static final String KEY_USER = "user";
    public static final String KEY_INVITE = "invite";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_FRIEND_GROUP = "isFriendGroup";
    public static final String KEY_FRIEND = "friend";
    public static final String KEY_MEMBER = "member";
    private static final String KEY_ROLE_CODE = "roleCode";
    public static final int OWNER_CODE = 10;
    public static final int MEMBER_CODE = 20;

    public Member() {

    }

    public static Member newMember(ParseUser user, Group group, boolean isFriendGroup, int roleCode) {
        Member member = new Member();
        member.put(KEY_USER,user);
        member.put(KEY_GROUP,group);
        member.put(KEY_IS_FRIEND_GROUP,isFriendGroup);
        member.put(KEY_ROLE_CODE,roleCode);
        member.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while saving member: " + e.getMessage(),e);
                    return;
                }
            }
        });
        return member;
    }

    public static Member newMember(ParseUser user, Group group, boolean isFriendGroup, ParseUser friend, int roleCode) {
        Member member = new Member();
        member.put(KEY_USER,user);
        member.put(KEY_GROUP,group);
        member.put(KEY_IS_FRIEND_GROUP,isFriendGroup);
        member.put(KEY_FRIEND,friend);
        member.put(KEY_ROLE_CODE,roleCode);
        member.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while saving member: " + e.getMessage(),e);
                    return;
                }
            }
        });
        return member;
    }

    public Group getGroup() {
        return (Group) getParseObject(KEY_GROUP);
    }

    public static List<Group> getGroups(List<Member> members) {
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            groups.add(members.get(i).getGroup());
        }
        return groups;
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public Invite getInvite() {
        return (Invite) getParseObject(KEY_INVITE);
    }

    public ParseUser getFriend() {
        return getParseUser(KEY_FRIEND);
    }

    public boolean getIsFriendGroup() {
        return getBoolean(KEY_IS_FRIEND_GROUP);
    }
}
