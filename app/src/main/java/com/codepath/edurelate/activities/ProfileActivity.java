package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    ToolbarMainBinding toolbarBinding;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"in on create");
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        toolbarBinding = ToolbarMainBinding.inflate(getLayoutInflater());
        setSupportActionBar(toolbarBinding.toolbarMain);

        setClickListeners();
    }

    private void setClickListeners() {
        Log.i(TAG,"setting on click listeners");
    }
}