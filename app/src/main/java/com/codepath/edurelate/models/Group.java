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
import java.util.Date;
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
    public static final String KEY_MEMBERS = "members";
    public static final String KEY_GROUP_PIC = "groupPic";
    public static final String KEY_MESSAGES = "messages";
    public static final String KEY_LATEST_MSG = "latestMsg";
    public static final String KEY_LATEST_MSG_DATE = "latestMsgDate";

    public static Group newNonFriendGroup(String groupName) throws ParseException {
        Group group = new Group();
        group.setGroupName(groupName);
        group.setNewMembers();
        group.put(KEY_MESSAGES,new ArrayList<>());
        group.setOwner(User.currentUser);
        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error saving group: " + e.getMessage(), e);
                    return;
                }
                group.setLatestMsgDate(group.getCreatedAt());
                Log.i(TAG,"Group successfully created!");
            }
        });
        User.addNewGroup(group,User.currentUser);
        return group;
    }

    public String getGroupName() {
        if (has(Group.KEY_GROUP_NAME)) {
            return getString(KEY_GROUP_NAME);
        }
        return "no name";
    }

    public boolean getIsFriendGroup() throws ParseException {
        return getOwner().getObjectId().equals(User.edurelateBot.getObjectId());
    }

    public ParseUser getOwner() throws ParseException {
        return fetchIfNeeded().getParseUser(KEY_OWNER);
    }

    public List<Message> getMessages() {
        return getList(KEY_MESSAGES);
    }

    public Chat getChat() {
        return (Chat) get(KEY_CHAT);
    }

    public List<Member> getMembers() {
        return getList(KEY_MEMBERS);
    }

    public ParseFile getGroupPic() throws ParseException {
        return fetchIfNeeded().getParseFile(KEY_GROUP_PIC);
    }

    public Message getLatestMsg() throws ParseException {
        return (Message) get(KEY_LATEST_MSG);
    }

    public Date getLatestMsgDate() throws ParseException {
        return getDate(KEY_LATEST_MSG_DATE);
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

    private void setLatestMsgDate(Date createdAt) {
        put(KEY_LATEST_MSG_DATE, createdAt);
    }

    public void addMember(Member member) {
        addUnique(KEY_MEMBERS,member);
    }

    private void addMessage(Message message) {
        add(KEY_MESSAGES,message);
    }

    public void setNewMembers() {
        List<Member> members = new ArrayList<>();
        put(KEY_MEMBERS,members);
    }

    public void setGroupPic(ParseFile groupPic) {
        put(KEY_GROUP_PIC,groupPic);
    }

//    public void setNewChat() {
//        this.chat = new Chat();
//        setChatFields();
//        chat.saveInBackground();
//        put(KEY_CHAT,this.chat);
//    }

    /* ------------- GROUP HELPER METHODS ------------------- */
    public void sendInvite(ParseUser user) {
        Invite.newInvite(user,this);
        // send messages to the sender and the recipient.
    }

    public Message sendNewMessage(String body, Message replyTo) {
        Message message = new Message();
        message.put(Message.KEY_BODY,body);
        message.put(Message.KEY_RECIPIENT,this);
        message.put(Message.KEY_USERS_LIKING_THIS, new ArrayList<>());
        message.put(Message.KEY_SENDER,User.currentUser);
        if (replyTo != null) {
            message.put(Message.KEY_REPLY_TO,replyTo);
        }
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error saving new message: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Message successfully saved.");
            }
        });
        put(KEY_LATEST_MSG,message);
        addMessage(message);
        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while adding message to group: " + e.getMessage(),e);
                }
                Log.i(TAG,"message successfully added to group");
            }
        });
        return message;
    }

    /* ------------- CHAT METHODS ------------------- */
//    public void setChatFields() {
//        this.chat.put(Chat.KEY_ISGROUPCHAT,true);
//        List<Message> messages = new ArrayList<>();
//        this.chat.put(Chat.KEY_MESSAGES,messages);
//    }
}
