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
import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.ProfileFragmentPagerAdapter;
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.fragments.NewGroupDialogFragment;
import com.codepath.edurelate.fragments.NewPicDialogFragment;
import com.codepath.edurelate.interfaces.ProfileFragmentInterface;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends BaseActivity implements ProfileFragmentInterface {

    public static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    Toolbar toolbar;
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
    }

    @Override
    public void logout() {
        goLoginActivity();
    }

    /* -------------------- FRAGMENT INTERFACE METHODS ---------------------- */
    @Override
    public void goChatActivity(Member member) {

    }

    @Override
    public void joinNewGroup() {
        HomeActivity.goAllGroupsActivity(this);
    }

    /* --------------------- intent methods to activities ----------------------- */
    private void goPeopleActivity() {
        Intent i = new Intent(ProfileActivity.this, PeopleActivity.class);
        i.putExtra(User.KEY_USER, Parcels.wrap(user));
        this.startActivity(i);
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void goChatActivity(Group group) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra(Group.KEY_GROUP,Parcels.wrap(group));
        i.putExtra(Member.KEY_FRIEND,user);
        startActivity(i);
    }
}