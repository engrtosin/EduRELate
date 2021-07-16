package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityGroupDetailsBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class GroupDetailsActivity extends AppCompatActivity {

    public static final String TAG = "GroupDetailsActivity";

    ActivityGroupDetailsBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        group = Parcels.unwrap(getIntent().getParcelableExtra(Group.KEY_GROUP));

        binding = ActivityGroupDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        try {
            initializeViews();
        } catch (ParseException e) {
            Log.e(TAG,"Error initializing views: " + e.getMessage(),e);
        }
        setClickListeners();
    }

    private void initializeViews() throws ParseException {
        Log.i(TAG,"Initializing views in " + TAG);
        tbMainBinding.tvActivityTitle.setText(group.getGroupName());
        setIconVisibility();
        initializeGroupSection();
        initializeOwnerSection();
    }

    private void initializeGroupSection() throws ParseException {
        ParseFile image = group.getGroupPic();
        if (image != null) {
            Log.i(TAG,"about to glide curr user pic");
            Glide.with(this).load(image.getUrl()).into(binding.ivGroupPic);
        }
        Log.i(TAG,"Created on: " + group.getCreatedAt());
    }

    private void initializeOwnerSection() throws ParseException {
        ParseFile image = group.getOwner().getParseFile(User.KEY_USER_PIC);
        if (image != null) {
            Log.i(TAG,"about to glide owner pic");
            Glide.with(this).load(image.getUrl()).into(binding.ivOwnerPic);
        }
        binding.tvOwnerName.setText(User.getFullName(group.getOwner()));
    }

    private void setIconVisibility() {
        if (User.compareUsers(group.getOwner(),User.currentUser)) {
            binding.ivOwnerChat.setVisibility(View.INVISIBLE);
            binding.ivEditPic.setVisibility(View.VISIBLE);
            return;
        }
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

        setToolbarClickListeners();
        HomeActivity.setBottomNavigationListener(bottomNavigation, GroupDetailsActivity.this);

        binding.tvActShowMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAllMembersActivity();
            }
        });
    }

    private void setToolbarClickListeners() {
        tbMainBinding.ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbMainBinding.ivLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"logout clicked");
                LoginActivity.logoutUser(GroupDetailsActivity.this);
            }
        });
    }

    private void goAllMembersActivity() {
        Intent i = new Intent(GroupDetailsActivity.this,AllMembersActivity.class);
        i.putExtra(Group.KEY_GROUP,group);
    }
}