package com.codepath.edurelate.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.UsersAdapter;
import com.codepath.edurelate.databinding.ActivityAllUsersBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.models.Chat;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllUsersActivity extends BaseActivity {

    public static final String TAG = "AllUsersActivity";
    public static final int SPAN_COUNT = 2;

    ActivityAllUsersBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    List<ParseUser> users = new ArrayList<>();
    UsersAdapter usersAdapter;
    GridLayoutManager glManager;
    Group invitingGroup;
    List<String> membersId;
    int inviteType;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inviteType = getIntent().getIntExtra(Invite.INVITE_TYPE,200);
        if (inviteType == Invite.GROUP_INVITE_CODE) {
            invitingGroup = Parcels.unwrap(getIntent().getParcelableExtra(Group.KEY_GROUP));
            membersId = new ArrayList<>();
            queryAllMemberIds();
        }

        Log.i(TAG,"in on create");
        binding = ActivityAllUsersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar("All Users");
//        tbMainBinding = ToolbarMainBinding.inflate(getLayoutInflater(), (ViewGroup) view);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        queryAllUsers();
        Log.i(TAG,"Number of all users: " + users.size());
        usersAdapter = new UsersAdapter(AllUsersActivity.this,users,inviteType,invitingGroup,membersId);
        setAdapterInterface();
        glManager = new GridLayoutManager(AllUsersActivity.this,SPAN_COUNT,
                GridLayoutManager.VERTICAL,false);
        binding.rvUsers.setAdapter(usersAdapter);
        binding.rvUsers.setLayoutManager(glManager);

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        Log.i(TAG,"Initializing views in " + TAG);
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

//        setToolbarClickListeners();
        HomeActivity.setBottomNavigationListener(bottomNavigation, AllUsersActivity.this);
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
                LoginActivity.logoutUser(AllUsersActivity.this);
            }
        });
    }

    /* --------------------- Parse Methods ------------------------ */
    protected void queryAllUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotContainedIn(User.KEY_OBJECT_ID,
                Arrays.asList(User.BOT_OBJECT_ID,ParseUser.getCurrentUser().getObjectId()));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while querying users: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG, "Users successfully queried. Size: " + objects.size());
                objects.add(0,ParseUser.getCurrentUser());
                usersAdapter.clear();
                usersAdapter.addAll(objects);
            }
        });
    }

    private void queryAllMemberIds() {
        membersId = new ArrayList<>();
        ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
        query.include(Member.KEY_USER);
        query.whereEqualTo(Member.KEY_GROUP,invitingGroup);
        query.findInBackground(new FindCallback<Member>() {
            @Override
            public void done(List<Member> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting members: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Members size: " + objects.size());
                for (int i = 0; i < objects.size(); i++) {
                    membersId.add(objects.get(i).getUser().getObjectId());
                }
            }
        });
    }

    private Chat findFriendChat(ParseUser user) {
        Chat chat = new Chat();
        ParseQuery<Chat> query = ParseQuery.getQuery(Chat.class);
        query.include(Chat.KEY_MESSAGES);
        return chat;
    }

    /* --------------------- adapter interface methods --------------------- */
    private void setAdapterInterface() {
        usersAdapter.setAdapterListener(new UsersAdapter.UsersAdapterInterface() {
            @Override
            public void userClicked(ParseUser user) {

                goProfileActivity(user);
            }

            @Override
            public void chatClicked(ParseUser user) {
                Chat chat = findFriendChat(user);
                goChatActivity(chat);
            }

            @Override
            public void inviteUser(ParseUser user) {
                if (inviteType == Invite.GROUP_INVITE_CODE) {
                    invitingGroup.sendInvite(user);
                }
            }

            @Override
            public void acceptInvite(Invite invite) {

            }

            @Override
            public void rejectInvite(Invite invite) {

            }

            @Override
            public void sendGroupInvite(ParseUser user, Group group) {
                group.sendInvite(user);
            }

            @Override
            public void joinGroup(ParseUser user, Group group) {
                group.addMember(user);
            }
        });
    }

    /* --------------------- intents to activities ------------------ */
    private void goProfileActivity(ParseUser owner) {
        Intent i = new Intent(AllUsersActivity.this, ProfileActivity.class);
        i.putExtra(User.KEY_USER, Parcels.wrap(owner));
        this.startActivity(i);
    }

    private void goChatActivity(Chat chat) {
        Intent i = new Intent(AllUsersActivity.this, ChatActivity.class);
        i.putExtra(Chat.KEY_CHAT, Parcels.wrap(chat));
        this.startActivity(i);
    }
}