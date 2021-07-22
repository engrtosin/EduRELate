package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.NotificationsAdapter;
import com.codepath.edurelate.databinding.ActivityNotificationsBinding;
import com.codepath.edurelate.models.Invite;
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
    List<Notification> notifications;
    NotificationsAdapter adapter;
    LinearLayoutManager llManager;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        setupToolbar("Notifications");

        notifications = new ArrayList<>();
        adapter = new NotificationsAdapter(this,notifications);
        binding.rvNotifications.setAdapter(adapter);
        llManager = new LinearLayoutManager(this);
        binding.rvNotifications.setLayoutManager(llManager);
        setupAdapterInterface();
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rvNotifications.addItemDecoration(itemDecoration);
        queryNotifications();
    }

    private void setupAdapterInterface() {
        adapter.setAdapterListener(new NotificationsAdapter.NotificationsAdapterInterface() {
            @Override
            public void inviteAccepted(Invite invite) {
                Log.i(TAG,"Invite accepted: " + invite.getObjectId());
            }

            @Override
            public void inviteRejected(Invite invite) {
                Log.i(TAG,"Invite rejected: " + invite.getObjectId());
            }
        });
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
                adapter.addAll(objects);
                Log.i(TAG,"Notifications gotten back. Size: " + notifications.size());
            }
        });
    }
}