package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.PeopleFragmentPagerAdapter;
import com.codepath.edurelate.databinding.ActivityPeopleBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.fragments.FriendsFragment;
import com.codepath.edurelate.fragments.GroupsFragment;
import com.codepath.edurelate.interfaces.PeopleFragmentInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PeopleActivity extends BaseActivity implements PeopleFragmentInterface {

    public static final String TAG = "PeopleActivity";

    ActivityPeopleBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    PeopleFragmentPagerAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPeopleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar("People");

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.action_home);
        adapter = new PeopleFragmentPagerAdapter(getSupportFragmentManager(),
                PeopleActivity.this);
        binding.vpPager.setAdapter(adapter);
        binding.slidingTabs.setupWithViewPager(binding.vpPager);
        setListeners();
    }

    private void setListeners() {
//        setToolbarClickListeners();
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

    /* --------------------- peopleInterface methods ---------------------- */
    @Override
    public void findNewFriend() {
        Log.i(TAG,"Find a new friend in people activity");
        HomeActivity.goAllUsersActivity(PeopleActivity.this);
    }

    @Override
    public void joinNewGroup() {
        HomeActivity.goAllGroupsActivity(PeopleActivity.this);
    }
}