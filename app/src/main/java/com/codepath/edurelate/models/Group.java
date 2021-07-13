package com.codepath.edurelate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;

@Parcel(analyze = Group.class)
@ParseClassName("Group")
public class Group extends ParseObject {

    public static final String TAG = "GroupModel";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_IS_FRIEND_GROUP = "isFriendGroup";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_USERS = "users";
    public static final String KEY_CHAT = "chat";

    public Chat chat;

    public String getGroupName() {
        return getString(KEY_GROUP_NAME);
    }

    public boolean getIsFriendGroup() {
        return getBoolean(KEY_IS_FRIEND_GROUP);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public Chat getChat() {
        return chat;
    }

    public List<ParseUser> getUsers() {
        return getList(KEY_USERS);
    }

    public void setGroupName(String groupName) {
        put(KEY_GROUP_NAME,groupName);
    }

    public void setIsFriendGroup(boolean isFriendGroup) {
        put(KEY_IS_FRIEND_GROUP,isFriendGroup);
    }

    public void setOwner(ParseUser owner) {
        put(KEY_OWNER,owner);
    }

    public void setNewChat() {
        this.chat = new Chat();
        put(KEY_CHAT,this.chat);
    }
}
