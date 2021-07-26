package com.codepath.edurelate.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

@Parcel(analyze = Notification.class)
@ParseClassName("Notification")
public class Notification extends ParseObject {

    public static final String TAG = "NotificationModel";
    public static final String KEY_USER = "user";
    public static final String KEY_NOTIF_TYPE = "notifType";
    public static final String KEY_NOTIF_TEXT = "notifText";
    public static final String KEY_INVITE = "invite";
    public static final String KEY_REQUEST = "request";
    public static final int INVITER_CODE = 10;
    public static final int INVITEE_CODE = 15;
    public static final int NEW_GROUP_CODE = 20;
    public static final int NEW_MEMBER_CODE = 25;
    public static final int REQUEST_RECEIVED_CODE = 30;
    public static final int REQUEST_SENT_CODE = 35;
    public static final int MEMBER_LEFT_CODE = 40;
    public static final int YOU_LEFT_GROUP_CODE = 45;

    /* ------------------------ NEW NOTIFICATION METHODS ------------------------ */
    public static Notification newNotification(ParseUser user, int notifType, String notifText, Invite invite) {
        Notification notification = new Notification();
        notification.put(KEY_USER,user);
        notification.put(KEY_NOTIF_TYPE,notifType);
        notification.put(KEY_NOTIF_TEXT,notifText);
        notification.put(KEY_INVITE,invite);
        notification.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while saving notification: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Notification successfully saved.");
            }
        });
        return notification;
    }

    public static Notification newNotification(ParseUser user, int notifType, String notifText, Request request) {
        Notification notification = new Notification();
        notification.put(KEY_USER,user);
        notification.put(KEY_NOTIF_TYPE,notifType);
        notification.put(KEY_NOTIF_TEXT,notifText);
        notification.put(KEY_REQUEST,request);
        notification.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while saving notification: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Notification successfully saved.");
            }
        });
        return notification;
    }

    public static Notification newNotification(ParseUser user, int notifType, String notifText) {
        Notification notification = new Notification();
        notification.put(KEY_USER,user);
        notification.put(KEY_NOTIF_TYPE,notifType);
        notification.put(KEY_NOTIF_TEXT,notifText);
        notification.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while saving notification: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Notification successfully saved.");
            }
        });
        return notification;
    }

    /* ------------------------ GET METHODS ------------------------ */
    public int getNotifType() {
        return getInt(KEY_NOTIF_TYPE);
    }

    public Invite getInvite() {
        return (Invite) getParseObject(KEY_INVITE);
    }

    public String getNotifText() {
        return getString(KEY_NOTIF_TEXT);
    }

    public Request getRequest() {
        return (Request) getParseObject(KEY_REQUEST);
    }
}
