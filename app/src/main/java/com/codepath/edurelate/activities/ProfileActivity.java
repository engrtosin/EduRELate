package com.codepath.edurelate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.fragments.NewGroupDialogFragment;
import com.codepath.edurelate.fragments.NewPicDialogFragment;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity implements NewPicDialogFragment.NewPicInterface {

    public static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.KEY_USER));

        Log.i(TAG,"in on create");
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
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
        tbMainBinding.tvActivityTitle.setText(User.getFirstName(user) + "'s Profile");
        setIconVisibilities();

        // TODO: Use User static method.
        ParseFile image = user.getParseFile(User.KEY_USER_PIC);
        Log.i(TAG,"user image: " + image);
        if (image != null) {
            Log.i(TAG,"about to glide user pic");
            Glide.with(this).load(image.getUrl()).into(binding.ivUserPic);
        }
        // TODO: Use User static method.
        binding.tvUsername.setText(user.getUsername());
        binding.tvPassword.setText(user.getString(User.KEY_PASSWORD));
        binding.tvFirstName.setText(User.getFirstName(user));
        binding.tvLastName.setText(User.getLastName(user));
    }

    private void setIconVisibilities() {
        if (User.compareUsers(user,ParseUser.getCurrentUser())) {
            binding.ivEditPic.setVisibility(View.VISIBLE);
            binding.tvDescPassword.setVisibility(View.VISIBLE);
            binding.tvPassword.setVisibility(View.VISIBLE);
            binding.ivChat.setVisibility(View.GONE);
            binding.tvActInvite.setVisibility(View.GONE);
            binding.tvActSendMsg.setVisibility(View.GONE);
        }
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

        setToolbarClickListeners();
        HomeActivity.setBottomNavigationListener(bottomNavigation,ProfileActivity.this);

        binding.btnPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPeopleActivity();
            }
        });
        binding.ivEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPicDialog();
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
                LoginActivity.logoutUser(ProfileActivity.this);
            }
        });
    }

    /* -------------------- new pic methods ---------------------- */
    private void showNewPicDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewPicDialogFragment newPicDialogFragment = NewPicDialogFragment.newInstance("New Pic");
        newPicDialogFragment.show(fm, "fragment_new_pic");
    }

    @Override
    public void picSaved(ParseFile parseFile) {
        User.setUserPic(user,parseFile);
        try {
            initializeViews();
        } catch (ParseException e) {
            Log.e(TAG,"Error initializing views: " + e.getMessage(),e);
        }
    }

    /* --------------------- intent methods to activities ----------------------- */
    private void goPeopleActivity() {
        Intent i = new Intent(ProfileActivity.this, PeopleActivity.class);
        i.putExtra(User.KEY_USER, Parcels.wrap(user));
        this.startActivity(i);
    }
}