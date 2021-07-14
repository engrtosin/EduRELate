package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.PeopleFragmentPagerAdapter;
import com.codepath.edurelate.databinding.ActivityPeopleBinding;
import com.codepath.edurelate.databinding.ActivityProfileBinding;

public class PeopleActivity extends AppCompatActivity {

    public static final String TAG = "PeopleActivity";

    ActivityPeopleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPeopleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.vpPager.setAdapter(new PeopleFragmentPagerAdapter(getSupportFragmentManager(),
                PeopleActivity.this));
        binding.slidingTabs.setupWithViewPager(binding.vpPager);
    }
}