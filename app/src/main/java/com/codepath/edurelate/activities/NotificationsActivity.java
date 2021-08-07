package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.NotificationsAdapter;
import com.codepath.edurelate.databinding.ActivityNotificationsBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Notification;
import com.codepath.edurelate.models.Request;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void setupAdapterInterface() {
        adapter.setAdapterListener(new NotificationsAdapter.NotificationsAdapterInterface() {
            @Override
            public void requestAccepted(Request request) {
                Log.i(TAG,"Invite accepted: " + request.getObjectId());
                request.getToGroup().acceptRequest(request);
            }

            @Override
            public void requestRejected(Request request) {
                Log.i(TAG,"Invite rejected: " + request.getObjectId());
                request.getToGroup().rejectRequest(request);
            }
        });
    }

    private void queryNotifications() {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.include(Notification.KEY_INVITE);
        query.include(Notification.KEY_REQUEST+"."+Request.KEY_TO_GROUP+"."+ Group.KEY_OWNER);
        query.include(Notification.KEY_REQUEST+"."+Request.KEY_CREATOR);
        query.include(Notification.KEY_USER);
        query.whereEqualTo(Notification.KEY_USER,ParseUser.getCurrentUser());
        query.orderByDescending(Notification.KEY_CREATED_AT);
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