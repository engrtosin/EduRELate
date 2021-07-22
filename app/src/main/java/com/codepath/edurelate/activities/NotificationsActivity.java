package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityNotificationsBinding;
import com.codepath.edurelate.models.Notification;

public class NotificationsActivity extends BaseActivity {

    public static final String TAG = "NotificationsActivity";

    ActivityNotificationsBinding binding;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        setupToolbar("Notifications");
    }
}