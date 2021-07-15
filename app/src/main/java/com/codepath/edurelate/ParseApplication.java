package com.codepath.edurelate;

import android.app.Application;

import com.codepath.edurelate.models.Chat;
import com.codepath.edurelate.models.Friend;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.Message;
import com.codepath.edurelate.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    public static final String TAG = "ParseApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Friend.class);
        ParseObject.registerSubclass(Chat.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Invite.class);
        ParseObject.registerSubclass(Member.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.applicationId))
                .clientKey(getString(R.string.clientKey))
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
