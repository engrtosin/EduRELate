package com.codepath.edurelate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.codepath.edurelate.activities.AllGroupsActivity;
import com.codepath.edurelate.adapters.GroupsAdapter;
import com.codepath.edurelate.databinding.FragmentGroupsBinding;
import com.codepath.edurelate.interfaces.PeopleFragmentInterface;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    public static final String TAG = "GroupsFragment";
    public static final int SPAN_COUNT = 2;

    FragmentGroupsBinding binding;
    PeopleFragmentInterface peopleListener;
    List<Group> groups;
    GroupsAdapter groupsAdapter;
    GridLayoutManager glManager;

    /* ------------ constructors ------------------ */
    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /* ------------ fragment lifecycle methods ------------------ */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGroupsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        peopleListener = (PeopleFragmentInterface) getActivity();
        setupRecyclerView();
        setClickListeners();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /* ------------- fragment setup methods --------------- */
    private void setupRecyclerView() {
        groups = new ArrayList<>();
        queryExtraGroups();
        Log.i(TAG,"Number of all groups: " + groups.size());
        groupsAdapter = new GroupsAdapter(getContext(),groups,null);
        setAdapterInterface();
        glManager = new GridLayoutManager(getContext(),SPAN_COUNT,
                GridLayoutManager.VERTICAL,false);
        binding.rvGroups.setAdapter(groupsAdapter);
        binding.rvGroups.setLayoutManager(glManager);
    }

    private void setAdapterInterface() {
    }

    private void setClickListeners() {
        binding.tvNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleListener.joinNewGroup();
            }
        });
    }

    private void queryExtraGroups() {
        ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
        query.include(Member.KEY_GROUP);
        query.whereEqualTo(Member.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Member.KEY_IS_FRIEND_GROUP,false);
        query.whereNotContainedIn(Member.KEY_GROUP,User.currUserGroups);
        query.findInBackground(new FindCallback<Member>() {
            @Override
            public void done(List<Member> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while querying for members: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Members queried successfully. Size: " + objects.size());
                User.currUserGroups.addAll(Member.getGroups(objects));
                groupsAdapter.addAll(User.currUserGroups);
            }
        });
    }
}