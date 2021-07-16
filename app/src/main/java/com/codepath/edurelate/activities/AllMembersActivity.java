package com.codepath.edurelate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.MembersAdapter;
import com.codepath.edurelate.adapters.UsersAdapter;
import com.codepath.edurelate.databinding.ActivityAllMembersBinding;
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.models.Chat;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllMembersActivity extends AppCompatActivity {

    public static final String TAG = "AllMembersActivity";
    public static final int SPAN_COUNT = 2;

    ActivityAllMembersBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    List<Member> members = new ArrayList<>();
    MembersAdapter membersAdapter;
    GridLayoutManager glManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Group group = Parcels.unwrap(getIntent().getParcelableExtra(Group.KEY_GROUP));

        Log.i(TAG,"in on create");
        binding = ActivityAllMembersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        members = group.getMembers();
        Log.i(TAG,"Number of all users: " + members.size());
        membersAdapter = new MembersAdapter(AllMembersActivity.this,members);
        setAdapterInterface();
        glManager = new GridLayoutManager(AllMembersActivity.this,SPAN_COUNT,
                GridLayoutManager.VERTICAL,false);
        binding.rvMembers.setAdapter(membersAdapter);
        binding.rvMembers.setLayoutManager(glManager);

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        Log.i(TAG,"Initializing views in " + TAG);
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

        setToolbarClickListeners();
        HomeActivity.setBottomNavigationListener(bottomNavigation, AllMembersActivity.this);
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
                LoginActivity.logoutUser(AllMembersActivity.this);
            }
        });
    }

    /* --------------------- Parse Methods ------------------------ */
    private Chat findFriendChat(ParseUser user) {
        Chat chat = new Chat();
        ParseQuery<Chat> query = ParseQuery.getQuery(Chat.class);
        query.include(Chat.KEY_MESSAGES);
        return chat;
    }

    /* --------------------- adapter interface methods --------------------- */
    private void setAdapterInterface() {
        membersAdapter.setAdapterListener(new MembersAdapter.MembersAdapterInterface() {
            @Override
            public void memberClicked(ParseUser user) {
                goProfileActivity(user);
            }

            @Override
            public void chatClicked(ParseUser user) {
                Chat chat = findFriendChat(user);
                goChatActivity(chat);
            }
        });
    }

    /* --------------------- intents to activities ------------------ */
    private void goProfileActivity(ParseUser user) {
        Intent i = new Intent(AllMembersActivity.this, ProfileActivity.class);
        i.putExtra(User.KEY_USER, Parcels.wrap(user));
        this.startActivity(i);
    }

    private void goChatActivity(Chat chat) {
        Intent i = new Intent(AllMembersActivity.this, ChatActivity.class);
        i.putExtra(Chat.KEY_CHAT, Parcels.wrap(chat));
        this.startActivity(i);
    }
}