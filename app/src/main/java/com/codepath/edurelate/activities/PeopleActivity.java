package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.PeopleFragmentPagerAdapter;
import com.codepath.edurelate.databinding.ActivityPeopleBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.fragments.FriendsFragment;
import com.codepath.edurelate.fragments.GroupsFragment;
import com.codepath.edurelate.interfaces.PeopleFragmentInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PeopleActivity extends AppCompatActivity implements PeopleFragmentInterface {

    public static final String TAG = "PeopleActivity";

    ActivityPeopleBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    PeopleFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPeopleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.action_home);
        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);
        adapter = new PeopleFragmentPagerAdapter(getSupportFragmentManager(),
                PeopleActivity.this);
        binding.vpPager.setAdapter(adapter);
        binding.slidingTabs.setupWithViewPager(binding.vpPager);
        setListeners();
    }

    private void setListeners() {
        setToolbarClickListeners();
        HomeActivity.setBottomNavigationListener(bottomNavigation,PeopleActivity.this);
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
                LoginActivity.logoutUser(PeopleActivity.this);
            }
        });
    }

    @Override
    public void findNewFriend() {
        HomeActivity.goAllUsersActivity(PeopleActivity.this);
    }

    @Override
    public void joinNewGroup() {
        HomeActivity.goAllGroupsActivity(PeopleActivity.this);
    }
}