package com.codepath.edurelate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityHomeBinding;
import com.codepath.edurelate.databinding.BottomNavigationBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HomeActivity";

    ActivityHomeBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    BottomNavigationBinding bottomNavBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.action_home);
        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);

        setClickListeners();
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

        setToolbarClickListeners();
        setBottomNavigationListener(bottomNavigation, HomeActivity.this);
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
                        goAllChatsActivity(activity);
                    }
                }
                else if (item.getItemId() == R.id.action_home) {
                    if (!(activity instanceof HomeActivity)) {
                        goHomeActivity(activity);
                    }
                }
                else if (item.getItemId() == R.id.action_profile) {
                    if (!(activity instanceof ProfileActivity)) {
                        goProfileActivity(activity);
                    }
                }
                return true;
            }
        });
    }

    /* ------------------ static navigation methods ----------------------- */
    public static void goAllChatsActivity(Activity activity) {
        Intent i = new Intent(activity, AllChatsActivity.class);
        activity.startActivity(i);
    }

    public static void goHomeActivity(Activity activity) {
        Intent i = new Intent(activity, HomeActivity.class);
        activity.startActivity(i);
    }

    public static void goProfileActivity(Activity activity) {
        Intent i = new Intent(activity, ProfileActivity.class);
        i.putExtra(LoginActivity.KEY_CURRENT_USER, Parcels.wrap(LoginActivity.currentUser));
        activity.startActivity(i);
    }
}