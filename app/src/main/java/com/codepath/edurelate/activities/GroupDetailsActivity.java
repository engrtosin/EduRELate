package com.codepath.edurelate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityGroupDetailsBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.fragments.NewPicDialogFragment;
import com.codepath.edurelate.models.Chat;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class GroupDetailsActivity extends BaseActivity implements NewPicDialogFragment.NewPicInterface {

    public static final String TAG = "GroupDetailsActivity";
    private static final int GO_ALL_USERS_CODE = 20;

    ActivityGroupDetailsBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    Group group;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        group = Parcels.unwrap(getIntent().getParcelableExtra(Group.KEY_GROUP));

        binding = ActivityGroupDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar(group.getGroupName());
//        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        try {
            initializeViews();
        } catch (ParseException e) {
            Log.e(TAG,"Error initializing views: " + e.getMessage(),e);
        }
        setClickListeners();
    }

    private void initializeViews() throws ParseException {
        Log.i(TAG,"Initializing views in " + TAG);
//        tbMainBinding.tvActivityTitle.setText(group.getGroupName());
        setIconVisibility();
        initializeGroupSection();
        initializeOwnerSection();
    }

    private void initializeGroupSection() throws ParseException {
        ParseFile image = group.getGroupPic();
        if (image != null) {
            Log.i(TAG,"about to glide curr user pic");
            Glide.with(this).load(image.getUrl()).into(binding.ivGroupPic);
        }
        Log.i(TAG,"Created on: " + group.getCreatedAt());
    }

    private void initializeOwnerSection() throws ParseException {
        ParseFile image;
        if (group.has(Group.KEY_GROUP_PIC)) {
            image = group.getOwner().getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Log.i(TAG,"about to glide owner pic");
                Glide.with(this).load(image.getUrl()).into(binding.ivOwnerPic);
            }
        }
        binding.tvOwnerName.setText(User.getFullName(group.getOwner()));
    }

    private void setIconVisibility() throws ParseException {
        if (User.compareUsers(group.getOwner(),ParseUser.getCurrentUser())) {
            binding.ivOwnerChat.setVisibility(View.INVISIBLE);
            binding.ivEditPic.setVisibility(View.VISIBLE);
            return;
        }
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

//        setToolbarClickListeners();
        HomeActivity.setBottomNavigationListener(bottomNavigation, GroupDetailsActivity.this);

        binding.tvActInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAllUsersActivityForResult();
            }
        });
        binding.ivEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPicDialog();
            }
        });
        binding.ivGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.tvActShowMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAllMembersActivity();
            }
        });
        binding.ivOwnerChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChatActivity(group.getOwner());
            }
        });
        binding.cvOwnerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goProfileActivity(group.getOwner());
            }
        });
        binding.tvOwnerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goProfileActivity(group.getOwner());
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
                LoginActivity.logoutUser(GroupDetailsActivity.this);
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
        group.setGroupPic(parseFile);
    }

    /* ------------------ intent methods to activities ---------------- */
    private void goAllMembersActivity() {
        Intent i = new Intent(GroupDetailsActivity.this,AllMembersActivity.class);
        i.putExtra(Group.KEY_GROUP,Parcels.wrap(group));
        this.startActivity(i);
    }

    private void goChatActivity(ParseUser owner) {
        Intent i = new Intent(GroupDetailsActivity.this,ChatActivity.class);
        i.putExtra(Chat.KEY_CHAT,Parcels.wrap(group.getChat()));
        this.startActivity(i);
    }

    private void goProfileActivity(ParseUser owner) {
        Log.i(TAG,"going to profile activity for: " + owner.getUsername());
        Intent i = new Intent(GroupDetailsActivity.this,ProfileActivity.class);
        i.putExtra(User.KEY_USER,Parcels.wrap(group.getOwner()));
        this.startActivity(i);
    }

    private void goAllUsersActivity() {
        Intent i = new Intent(GroupDetailsActivity.this, AllUsersActivity.class);
        i.putExtra(Group.KEY_GROUP,Parcels.wrap(group));
        i.putExtra(Invite.INVITE_TYPE,Invite.GROUP_INVITE_CODE);
        this.startActivity(i);
    }

    private void goAllUsersActivityForResult() {
        Intent i = new Intent(GroupDetailsActivity.this,AllUsersActivity.class);
        i.putExtra(Group.KEY_GROUP,Parcels.wrap(group));
        i.putExtra(Invite.INVITE_TYPE,Invite.GROUP_INVITE_CODE);
        this.startActivityForResult(i,GO_ALL_USERS_CODE);
    }

    /* ---------------------- onActivityResult ----------------------- */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_ALL_USERS_CODE) {

        }
    }
}