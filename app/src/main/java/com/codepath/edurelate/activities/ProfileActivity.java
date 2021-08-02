package com.codepath.edurelate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityOptionsCompat;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.ProfileFragmentPagerAdapter;
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.interfaces.ProfileFragmentInterface;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ProfileActivity extends BaseActivity implements ProfileFragmentInterface {

    public static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    BottomNavigationView bottomNavigation;
    ParseUser user;
    ProfileFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.KEY_USER));

        Log.i(TAG,"in on create");
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar(User.getFullName(user));
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        adapter = new ProfileFragmentPagerAdapter(getSupportFragmentManager(),this,user);
        binding.vpPager.setAdapter(adapter);
        binding.slidingTabs.setupWithViewPager(binding.vpPager);
        HomeActivity.setBottomNavigationListener(bottomNavigation,this);
    }

    @Override
    public void logout() {
        LoginActivity.logoutUser(this);
    }

    /* -------------------- FRAGMENT INTERFACE METHODS ---------------------- */
    @Override
    public void goChatActivity(Member member) {

    }

    @Override
    public void joinNewGroup() {
        HomeActivity.goAllGroupsActivity(this);
    }

    @Override
    public void goToGroup(Group group) {
        goGroupActivity(group);
    }

    /* --------------------- intent methods to activities ----------------------- */
    private void goGroupActivity(Group group) {
        Intent i = new Intent(ProfileActivity.this, GroupDetailsActivity.class);
        i.putExtra(Group.KEY_GROUP,Parcels.wrap(group));
        this.startActivity(i);
    }

    private void goChatActivity(Group group) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra(Group.KEY_GROUP,Parcels.wrap(group));
        i.putExtra(Member.KEY_FRIEND,user);
        startActivity(i);
    }
}