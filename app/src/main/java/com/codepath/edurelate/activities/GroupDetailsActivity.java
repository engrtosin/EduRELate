package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityGroupDetailsBinding;

public class GroupDetailsActivity extends AppCompatActivity {

    public static final String TAG = "GroupDetailsActivity";

    ActivityGroupDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGroupDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}