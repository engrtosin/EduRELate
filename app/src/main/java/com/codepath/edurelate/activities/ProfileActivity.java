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
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.fragments.NewGroupDialogFragment;
import com.codepath.edurelate.fragments.NewPicDialogFragment;
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

public class ProfileActivity extends BaseActivity implements NewPicDialogFragment.NewPicInterface {

    public static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    Toolbar toolbar;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.KEY_USER));

        Log.i(TAG,"in on create");
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar(User.getFullName(user));
//        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
//        tbMainBinding.tvActivityTitle.setText(User.getFirstName(user) + "'s Profile");
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

//        setToolbarClickListeners();
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
        binding.ivLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                goLoginActivity();
            }
        });
        binding.ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"On click chat");
                // TODO: Create a helper method
                // I can also query once the user gets into this room.
                ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
                query.include(Member.KEY_USER);
                query.include(Member.KEY_GROUP);
                query.whereEqualTo(Member.KEY_FRIEND,user);
                query.whereContainedIn(Member.KEY_USER, Arrays.asList(ParseUser.getCurrentUser()));
                query.findInBackground(new FindCallback<Member>() {
                    @Override
                    public void done(List<Member> objects, ParseException e) {
                        if (e != null) {
                            Log.e(TAG,"Error while trying to get member object: " + e.getMessage(),e);
                            return;
                        }
                        if (objects.size() != 0) {
                            Log.i(TAG,"Member object found for a chat: ");
                            goChatActivity(objects.get(0).getGroup());
                            return;
                        }
                        Log.i(TAG,"Member object not found for a chat: ");
                        Group group = Group.newFriendGroup(ParseUser.getCurrentUser(),user);
                        goChatActivity(group);
                    }
                });
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
        initializeViews();
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