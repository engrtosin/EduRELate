package com.codepath.edurelate.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityGroupDetailsBinding;
import com.codepath.edurelate.fragments.AboutGroupFragment;
import com.codepath.edurelate.fragments.ChatFragment;
import com.codepath.edurelate.fragments.MembersFragment;
import com.codepath.edurelate.interfaces.GroupDetailsInterface;
import com.codepath.edurelate.models.Category;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

public class GroupDetailsActivity extends BaseActivity implements GroupDetailsInterface {

    public static final String TAG = "GroupDetailsActivity";
    private static final int GO_ALL_USERS_CODE = 20;

    ActivityGroupDetailsBinding binding;
    BottomNavigationView bottomNavigation;
    Member member;
    Group group;
    MenuItem currItem;
    boolean drawerState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        group = Parcels.unwrap(getIntent().getParcelableExtra(Group.KEY_GROUP));
        Log.i(TAG,"Group: " + group.getGroupName());
        queryMember();
        binding = ActivityGroupDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar(group.getGroupName());
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.action_home);
        setListeners();
    }

    private void setListeners() {
        HomeActivity.setBottomNavigationListener(bottomNavigation,this);
        binding.drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull @NotNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull @NotNull View drawerView) {
                drawerState = true;
            }

            @Override
            public void onDrawerClosed(@NonNull @NotNull View drawerView) {
                drawerState = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        binding.cvFragTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawerState();
            }
        });
        currItem = binding.navView.getCheckedItem();
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                if (!item.equals(currItem)) {
                    currItem = item;
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    if (item.getItemId() == R.id.action_about) {
                        binding.tvFragTitle.setText(getString(R.string.about));
                        ft.replace(R.id.flContainer,AboutGroupFragment.newInstance(member,group));
                        ft.commit();
                        binding.drawerLayout.close();
                        return true;
                    }
                    if (item.getItemId() == R.id.action_members) {
                        binding.tvFragTitle.setText(getString(R.string.members));
                        ft.replace(R.id.flContainer, MembersFragment.newInstance(group));
                        ft.commit();
                        binding.drawerLayout.close();
                        return true;
                    }
                    if (item.getItemId() == R.id.action_chat) {
                        binding.tvFragTitle.setText(getString(R.string.group_chat));
                        ft.replace(R.id.flContainer, ChatFragment.newInstance(group));
                        ft.commit();
                        binding.drawerLayout.close();
                        return true;
                    }
                    if (item.getItemId() == R.id.action_forum) {
                        binding.tvFragTitle.setText(getString(R.string.group_forum));
                        ft.replace(R.id.flContainer,AboutGroupFragment.newInstance(member,group));
                        ft.commit();
                        binding.drawerLayout.close();
                        return true;
                    }
                    if (item.getItemId() == R.id.action_files) {
                        binding.tvFragTitle.setText(getString(R.string.group_files));
                        ft.replace(R.id.flContainer,AboutGroupFragment.newInstance(member,group));
                        ft.commit();
                        binding.drawerLayout.close();
                        return true;
                    }
                }
                return true;
            }
        });
        binding.navView.setCheckedItem(R.id.action_about);
    }

    private void toggleDrawerState() {
        if (drawerState) {
            binding.drawerLayout.close();
            return;
        }
        binding.drawerLayout.open();
    }

    private void queryMember() {
        ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
        query.include(Member.KEY_GROUP+"."+Group.KEY_CATEGORIES);
        query.include(Member.KEY_GROUP+"."+Group.KEY_OWNER);
        query.include(Member.KEY_USER);
        query.whereEqualTo(Member.KEY_USER,ParseUser.getCurrentUser());
        query.whereContainedIn(Member.KEY_GROUP, Arrays.asList(group));
        query.findInBackground(new FindCallback<Member>() {
            @Override
            public void done(List<Member> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error occurred while querying member: " + e.getMessage(),e);
                    return;
                }
                if (objects.size() > 0) {
                    Log.i(TAG,"Member queried successfully");
                    member = objects.get(0);
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                binding.tvFragTitle.setText(getString(R.string.about));
                ft.replace(R.id.flContainer,AboutGroupFragment.newInstance(member,group));
                ft.commit();
                Log.i(TAG,"Member queried successfully");
            }
        });
    }

    /* ------------------------ HELPER METHODS ------------------------ */
    private void leaveGroup() {
        User.leaveGroup(group);
        HomeActivity.navHomeActivity(this);
    }

    /* ------------------ INTERFACE METHODS ---------------- */
    @Override
    public void leave() {
        leaveGroup();
    }

    @Override
    public void inviteUser() {

    }

    @Override
    public void chatWithUser(ParseUser user) {

    }

    @Override
    public void goProfile(ParseUser user) {

    }

    /* ------------------ intent methods to activities ---------------- */
    private void goChatActivity(ParseUser owner) {

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