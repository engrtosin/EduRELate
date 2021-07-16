package com.codepath.edurelate.models;

import android.util.Log;

import com.codepath.edurelate.activities.LoginActivity;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = Group.class)
@ParseClassName("Group")
public class Group extends ParseObject {

    public static final String TAG = "GroupModel";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_IS_FRIEND_GROUP = "isFriendGroup";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_CHAT = "chat";
    public static final String KEY_GROUP = "group";
    private static final String KEY_MEMBERS = "members";
    private static final String KEY_GROUP_PIC = "groupPic";

    public Chat chat;

    public static Group newGroup(String groupName) {
        Group group = new Group();
        group.setGroupName(groupName);
        group.setIsFriendGroup(false);
        group.setNewMembers();
        group.setNewChat();
        group.setOwner(User.currentUser);
        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error saving group: " + e.getMessage(), e);
                    return;
                }
                Log.i(TAG,"Group successfully created!");
            }
        });
        User.addNewGroup(group,User.currentUser);
        return group;
    }

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

    public List<Member> getMembers() {
        return getList(KEY_MEMBERS);
    }

    public ParseFile getGroupPic() throws ParseException {
        return fetchIfNeeded().getParseFile(KEY_GROUP_PIC);
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

    public void addMember(Member member) {
        addUnique(KEY_MEMBERS,member);
    }

    public void setNewMembers() {
        List<Member> members = new ArrayList<>();
        put(KEY_MEMBERS,members);
    }

    public void setGroupPic(ParseFile groupPic) {
        put(KEY_GROUP_PIC,groupPic);
    }

    public void setNewChat() {
        this.chat = new Chat();
        setChatFields();
        chat.saveInBackground();
        put(KEY_CHAT,this.chat);
    }

    /* ------------- CHAT METHODS ------------------- */
    public void setChatFields() {
        this.chat.put(Chat.KEY_ISGROUPCHAT,true);
        List<Message> messages = new ArrayList<>();
        this.chat.put(Chat.KEY_MESSAGES,messages);
    }
}
