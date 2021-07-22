package com.codepath.edurelate;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codepath.edurelate.activities.HomeActivity;
import com.codepath.edurelate.activities.LoginActivity;
import com.codepath.edurelate.activities.ProfileActivity;
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    BottomNavigationView bottomNavigation;
    public Toolbar toolbar;
    public TextView tvActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"in on create");
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        setupToolbar("Base Activity");

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        Log.i(TAG,"Initializing views in " + TAG);
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

        HomeActivity.setBottomNavigationListener(bottomNavigation, BaseActivity.this);
    }

    protected void setupToolbar(String title) {
        toolbar = findViewById(R.id.tbOrigMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setActivityTitle(title);
    }

    protected void setActivityTitle(String title) {
        tvActivityTitle = toolbar.findViewById(R.id.tvActivityTitle);
        tvActivityTitle.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            HomeActivity.goNotificationsActivity(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
