package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.adapters.UsersAdapter;
import com.codepath.edurelate.databinding.FragmentMembersBinding;
import com.codepath.edurelate.interfaces.GroupDetailsInterface;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Member;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MembersFragment extends Fragment {

    public static final String TAG = "MembersFragment";
    public static final int SPAN_COUNT = 2;

    GroupDetailsInterface mListener;
    FragmentMembersBinding binding;
    View rootView;
    UsersAdapter adapter;
    Group group;
    List<ParseUser> members;
    List<String> memberIds;
    GridLayoutManager glManager;

    public MembersFragment() {
        // Required empty public constructor
    }

    public static MembersFragment newInstance(Group group) {
        MembersFragment fragment = new MembersFragment();
        Bundle args = new Bundle();
        args.putParcelable(Group.KEY_GROUP,group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = getArguments().getParcelable(Group.KEY_GROUP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMembersBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        members = new ArrayList<>();
        memberIds = new ArrayList<>();
        queryAllMembers();
        adapter = new UsersAdapter(getContext(),members,-1,group,memberIds);
        setAdapterInterface();
        glManager = new GridLayoutManager(getContext(),SPAN_COUNT,
                GridLayoutManager.VERTICAL,false);
        binding.rvMembers.setAdapter(adapter);
        binding.rvMembers.setLayoutManager(glManager);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener = (GroupDetailsInterface) getActivity();
    }

    private void queryAllMembers() {
        ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
        query.include(Member.KEY_USER);
        query.whereEqualTo(Member.KEY_GROUP,group);
        query.findInBackground(new FindCallback<Member>() {
            @Override
            public void done(List<Member> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting members: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Members size: " + objects.size());
                for (int i = 0; i < objects.size(); i++) {
                    members.add(objects.get(i).getUser());
                    memberIds.add(members.get(i).getObjectId());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /* --------------------- adapter interface methods --------------------- */
    private void setAdapterInterface() {
        adapter.setAdapterListener(new UsersAdapter.UsersAdapterInterface() {
            @Override
            public void userClicked(ParseUser user) {
                mListener.goProfile(user);
            }

            @Override
            public void chatClicked(ParseUser user) {

            }

            @Override
            public void inviteUser(ParseUser user) {
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
}