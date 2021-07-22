package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityNotificationsBinding;
import com.codepath.edurelate.models.Notification;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends BaseActivity {

    public static final String TAG = "NotificationsActivity";

    ActivityNotificationsBinding binding;
    List<Notification> notifications = new ArrayList<>();
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        setupToolbar("Notifications");

        queryNotifications();
    }

    private void queryNotifications() {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.include(Notification.KEY_INVITE);
        query.include(Notification.KEY_USER);
        query.whereEqualTo(Notification.KEY_USER,ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while getting notifications: " + e.getMessage(),e);
                    return;
                }
                notifications.addAll(objects);
                Log.i(TAG,"Notifications gotten back. Size: " + notifications.size());
            }
        });
    }
}