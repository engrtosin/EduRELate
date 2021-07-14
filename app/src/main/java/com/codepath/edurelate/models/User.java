package com.codepath.edurelate.models;

import com.parse.ParseException;
import com.parse.ParseUser;

public class User {

    public static final String TAG = "UserModel";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static Message sendMessage(String body, ParseUser sender, Group recipient, Message replyTo) throws ParseException {
        Message message = new Message();
        message.put(Message.KEY_BODY, body);
        message.put(Message.KEY_SENDER, sender);
        message.put(Message.KEY_REPLY_TO, replyTo);
        message.put(Message.KEY_RECIPIENT, recipient);
        message.save();
        recipient.getChat().addMessage(message);
        return message;
    }

    public static void updateInviteStatus(Invite invite) {
        // add the recipient to the group or as a friend to sender
        // send a message to the recipient
        // send a message to the sender
    }
}
