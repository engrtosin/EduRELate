package com.codepath.edurelate.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.parceler.Parcel;

@Parcel(analyze = Notification.class)
@ParseClassName("Notification")
public class Notification extends ParseObject {

    public static final String TAG = "NotificationModel";
    public static final String KEY_NOTIF_TYPE = "notifType";
    public static final String KEY_NOTIF_TEXT = "notifText";
    public static final String KEY_INVITE = "invite";
    public static final int INVITER_CODE = 10;
    public static final int INVITEE_CODE = 20;

    public static Notification newInstance(int notifType, String notifText, Invite invite) {
        Notification notification = new Notification();
        notification.put(KEY_NOTIF_TYPE,notifType);
        notification.put(KEY_NOTIF_TEXT,notifText);
        notification.put(KEY_INVITE,invite);
        notification.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while saving notification: " + e.getMessage(),e);
                }
                Log.i(TAG,"Notification successfully saved.");
            }
        });
        return notification;
    }
}
