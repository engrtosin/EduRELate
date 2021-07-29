package com.codepath.edurelate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.edurelate.BaseActivity;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.GroupsAdapter;
import com.codepath.edurelate.databinding.ActivityAllGroupsBinding;
import com.codepath.edurelate.databinding.ToolbarMainBinding;
import com.codepath.edurelate.fragments.NewGroupDialogFragment;
import com.codepath.edurelate.fragments.SearchChatsFragment;
import com.codepath.edurelate.fragments.SearchGroupsFragment;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Request;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class AllGroupsActivity extends BaseActivity {

    public static final String TAG = "AllGroupsActivity";
    public static final int SPAN_COUNT = 2;

    ActivityAllGroupsBinding binding;
    ToolbarMainBinding tbMainBinding;
    BottomNavigationView bottomNavigation;
    List<Group> groups = new ArrayList<>();
    List<String> requestIds;
    GroupsAdapter groupsAdapter;
    GridLayoutManager glManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"in on create");
        binding = ActivityAllGroupsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar("All Groups");
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        groups = new ArrayList<>();
        requestIds = new ArrayList<>();
        queryCurrUserRequests();
        Log.i(TAG,"Number of all users: " + groups.size());
        groupsAdapter = new GroupsAdapter(AllGroupsActivity.this,groups,requestIds);
        setAdapterInterface();
        glManager = new GridLayoutManager(AllGroupsActivity.this,SPAN_COUNT,
                GridLayoutManager.VERTICAL,false);
        binding.rvGroups.setAdapter(groupsAdapter);
        binding.rvGroups.setLayoutManager(glManager);
        queryAllGroups();

        setClickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        menu.findItem(R.id.action_search).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            switchToSearch();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

        HomeActivity.setBottomNavigationListener(bottomNavigation,AllGroupsActivity.this);

        binding.tvActNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewGroupDialog();
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
                LoginActivity.logoutUser(AllGroupsActivity.this);
            }
        });
    }

    private void showNewGroupDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewGroupDialogFragment newGroupDialogFragment = NewGroupDialogFragment.newInstance("New Group");
        newGroupDialogFragment.show(fm, "fragment_new_group");
    }

    private void queryAllGroups() {
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.include(Group.KEY_OWNER);
        query.whereNotEqualTo(Group.KEY_OWNER,User.edurelateBot);
        query.whereNotContainedIn(Group.KEY_OBJECT_ID,User.getCurrGroupIds());
        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while querying groups: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG, "Groups successfully queried. Size: " + objects.size());
                groupsAdapter.clear();
                groupsAdapter.addAll(objects);
            }
        });
    }

    private void queryCurrUserRequests() {
        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.include(Request.KEY_TO_GROUP);
        query.whereEqualTo(Request.KEY_CREATOR,ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while querying request: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Request successfully queried");
                requestIds.addAll(Request.getGroupIds(objects));
            }
        });
    }

    /* -------------------------- SEARCH -------------------------------- */
    private void switchToSearch() {
        binding.tvActNewGroup.setVisibility(View.GONE);
        binding.rvGroups.setVisibility(View.GONE);
        binding.flContainer.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SearchGroupsFragment fragment = SearchGroupsFragment.newInstance(groups);
        fragment.setFragListener(new SearchGroupsFragment.SearchFragInterface() {
            @Override
            public void fragmentClosed() {
                binding.tvActNewGroup.setVisibility(View.VISIBLE);
                binding.rvGroups.setVisibility(View.VISIBLE);
                binding.flContainer.setVisibility(View.GONE);
            }
        });
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
    }

    /* ------------------- interface methods ------------------ */
    private void setAdapterInterface() {
        groupsAdapter.setAdapterListener(new GroupsAdapter.GroupsAdapterInterface() {
            @Override
            public void groupClicked(Group group,View groupPic) {
                goGroupDetailsActivity(group);
            }

            @Override
            public void ownerClicked(ParseUser owner) {
                goProfileActivity(owner);
            }

            @Override
            public void joinGroup(Group group) {
                User.sendGroupRequest(group);
            }
        });
    }

    /* --------------------- intent methods to activities --------------------- */
    private void goProfileActivity(ParseUser owner) {
        Intent i = new Intent(AllGroupsActivity.this, ProfileActivity.class);
        i.putExtra(User.KEY_USER, Parcels.wrap(owner));
        this.startActivity(i);
    }

    private void goGroupDetailsActivity(Group group) {
        Intent i = new Intent(AllGroupsActivity.this, GroupDetailsActivity.class);
        i.putExtra(Group.KEY_GROUP,Parcels.wrap(group));
        this.startActivity(i);
    }
}