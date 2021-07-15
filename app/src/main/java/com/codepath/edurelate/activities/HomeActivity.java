package com.codepath.edurelate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
//        bottomNavBinding = BottomNavigationBinding.inflate(getLayoutInflater(),(ViewGroup) view);
//        Log.i(TAG,"bottom navigation inflated");
//        bottomNavBinding.bottomNavigation.setSelectedItemId(R.id.action_home);
        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);
        setClickListeners();
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

        tbMainBinding.ivLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"logout clicked");
                LoginActivity.logoutUser(HomeActivity.this);
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Log.i(TAG,"bottom navigation item selected");
                // TODO: correct if-else structure to remove else if possible
                if (item.getItemId() == R.id.action_chats) {
                    goAllChatsActivity();
                }
                else if (item.getItemId() == R.id.action_profile) {
                    goProfileActivity();
                }
                return true;
            }
        });
    }

    private void goAllChatsActivity() {
        Intent i = new Intent(this, AllChatsActivity.class);
        startActivity(i);
    }

    private void goProfileActivity() {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(LoginActivity.KEY_CURRENT_USER, Parcels.wrap(LoginActivity.currentUser));
        startActivity(i);
    }
}