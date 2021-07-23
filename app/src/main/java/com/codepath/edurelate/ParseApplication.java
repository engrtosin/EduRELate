package com.codepath.edurelate;

import android.app.Application;
import android.widget.Toast;

import com.codepath.edurelate.models.Chat;
import com.codepath.edurelate.models.Friend;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.Message;
import com.codepath.edurelate.models.Notification;
import com.codepath.edurelate.models.Request;
import com.codepath.edurelate.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
        ParseObject.registerSubclass(Notification.class);
        ParseObject.registerSubclass(Request.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.applicationId))
                .clientKey(getString(R.string.clientKey))
                .server("https://parseapi.back4app.com")
                .build()
        );
    }

    public void deleteGroup(String groupId) {
        // delete the group

// Retrieve the object by id
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.getInBackground(groupId, (object, e) -> {
            if (e == null) {
                // Deletes the fetched ParseObject from the database
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                    }else{
                        //Something went wrong while deleting the Object
                        Toast.makeText(this, "Error: "+e2.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                //Something went wrong while retrieving the Object
                Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // delete the chat
        // remove group from current user group list
    }
}
