package com.codepath.edurelate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.edurelate.databinding.FragmentFriendsBinding;
import com.codepath.edurelate.databinding.FragmentGroupsBinding;

import org.jetbrains.annotations.NotNull;

public class GroupsFragment extends Fragment {

    public static final String TAG = "GroupsFragment";

    FragmentGroupsBinding binding;

    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        setClickListeners();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setClickListeners() {
        binding.tvNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}