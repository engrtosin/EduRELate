package com.codepath.edurelate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    ToolbarMainBinding tbMainBinding;
    Toolbar toolbar;
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"in on create");
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater());
//        tbMainBinding.tbMain.setNavigationIcon(R.drawable.outline_logout_black_24dp);
//        tbMainBinding.tvTitle.setText("Profile");
        toolbar = findViewById(R.id.tbMain);
        toolbar.setNavigationIcon(R.drawable.outline_logout_black_24dp);
        setSupportActionBar(toolbar);
        setClickListeners();
    }

    private void setClickListeners() {
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"logout clicked");
                LoginActivity.logoutUser(ProfileActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ((TextView) toolbar.findViewById(R.id.tvTitle)).setText("Profile");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(TAG,"item clicked: " + item.getItemId());
        if (item.getItemId() == R.id.action_logout) {
            Log.i(TAG,"logout clicked");
            LoginActivity.logoutUser(ProfileActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}