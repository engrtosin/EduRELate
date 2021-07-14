package com.codepath.edurelate.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.edurelate.databinding.ActivityAllChatsBinding;
import com.codepath.edurelate.databinding.ActivityAllMembersBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;

public class AllChatsActivity extends AppCompatActivity {

    public static final String TAG = "AllChatsActivity";

    ActivityAllChatsBinding binding;
    ToolbarMainBinding tbMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"in on create");
        binding = ActivityAllChatsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);

        setClickListeners();
    }

    private void setClickListeners() {
        tbMainBinding.ivLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"logout clicked");
                LoginActivity.logoutUser(AllChatsActivity.this);
            }
        });
    }
}