package com.codepath.edurelate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.GroupsAdapter;
import com.codepath.edurelate.databinding.ActivityHomeBinding;
import com.codepath.edurelate.databinding.TitleActivityBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HomeActivity";
    public static final int SPAN_COUNT = 2;

    ActivityHomeBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    List<Group> groups;
    GroupsAdapter groupsAdapter;
    GridLayoutManager glManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.action_home);
        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);

        groups = User.getNonFriendGroups(User.currentUser);
        Log.i(TAG,"Number of current user's groups: " + groups.size());
        groupsAdapter = new GroupsAdapter(HomeActivity.this,groups);
        setAdapterInterface();
        glManager = new GridLayoutManager(HomeActivity.this,SPAN_COUNT,
                GridLayoutManager.VERTICAL,false);
        binding.rvGroups.setAdapter(groupsAdapter);
        binding.rvGroups.setLayoutManager(glManager);

        try {
            initializeViews();
        } catch (ParseException e) {
            Log.e(TAG,"Error while initializing views: " + e.getMessage(), e);
        }
        setClickListeners();
    }

    private void initializeViews() throws ParseException {
        setToolbarTitle(binding.getRoot(),getString(R.string.home_title));
        tbMainBinding.tvActivityTitle.setText(getString(R.string.home_title));
        ParseFile image = User.currentUser.getParseFile(User.KEY_USER_PIC);
        Log.i(TAG, User.currentUser.getUsername());
        Log.i(TAG,"curr user image: " + image);
        if (image != null) {
            Log.i(TAG,"about to glide curr user pic");
            Glide.with(this).load(image.getUrl()).into(binding.ivUserPic);
        }
        binding.tvName.setText(User.getFullName(User.currentUser));
        binding.tvUsername.setText("@" + User.currentUser.getUsername());
    }

    public static void setToolbarTitle(View rootView, String title) {
        TextView tvActivityTitle = rootView.findViewById(R.id.tvActivityTitle);
        tvActivityTitle.setText(title);
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");
        setToolbarClickListeners();
        setBottomNavigationListener(bottomNavigation, HomeActivity.this);
        setUserClickListeners();
        binding.tvActPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPeopleActivity();
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
                LoginActivity.logoutUser(HomeActivity.this);
            }
        });
    }

    public static void setBottomNavigationListener(BottomNavigationView bottomNavigation, Activity activity) {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Log.i(TAG,"bottom navigation item selected");
                // TODO: correct if-else structure to remove else if possible
                if (item.getItemId() == R.id.action_chats) {
                    if (!(activity instanceof AllChatsActivity)) {
                        navAllChatsActivity(activity);
                    }
                }
                else if (item.getItemId() == R.id.action_home) {
                    if (!(activity instanceof HomeActivity)) {
                        navHomeActivity(activity);
                    }
                }
                else if (item.getItemId() == R.id.action_profile) {
                    if (!(activity instanceof ProfileActivity)) {
                        navProfileActivity(activity);
                    }
                }
                return true;
            }
        });
    }

    private void setUserClickListeners() {
        binding.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navProfileActivity(HomeActivity.this);
            }
        });
        binding.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navProfileActivity(HomeActivity.this);
            }
        });
        binding.cvUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navProfileActivity(HomeActivity.this);
            }
        });
    }

    /* ------------------------- interface methods --------------------------- */
    private void setAdapterInterface() {
        groupsAdapter.setAdapterListener(new GroupsAdapter.GroupsAdapterInterface() {
            @Override
            public void groupClicked(Group group) {
                goGroupDetailsActivity(group);
            }

            @Override
            public void ownerClicked(ParseUser owner) {
                goProfileActivity(owner);
            }
        });
    }

    /* --------------------- intent navigation methods ------------------------ */
    private void goPeopleActivity() {
        Intent i = new Intent(HomeActivity.this, PeopleActivity.class);
        this.startActivity(i);
    }

    private void goProfileActivity(ParseUser owner) {
        Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
        i.putExtra(User.KEY_USER,Parcels.wrap(owner));
        this.startActivity(i);
    }

    private void goGroupDetailsActivity(Group group) {
        Intent i = new Intent(HomeActivity.this, GroupDetailsActivity.class);
        i.putExtra(Group.KEY_GROUP,Parcels.wrap(group));
        this.startActivity(i);
    }

    /* ------------------ static activity intent methods ----------------------- */
    public static void navAllChatsActivity(Activity activity) {
        Intent i = new Intent(activity, AllChatsActivity.class);
        activity.startActivity(i);
    }

    public static void navHomeActivity(Activity activity) {
        Intent i = new Intent(activity, HomeActivity.class);
        activity.startActivity(i);
    }

    public static void navProfileActivity(Activity activity) {
        Intent i = new Intent(activity, ProfileActivity.class);
        i.putExtra(User.KEY_USER, Parcels.wrap(User.currentUser));
        activity.startActivity(i);
    }

    public static void goAllGroupsActivity(Activity activity) {
        Intent i = new Intent(activity, AllGroupsActivity.class);
        activity.startActivity(i);
    }

    public static void goAllUsersActivity(Activity activity) {
        Log.i(TAG,"Go to all users activity from " + activity.getLocalClassName());
        Intent i = new Intent(activity, AllUsersActivity.class);
        activity.startActivity(i);
    }
}