package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.FragmentFriendsBinding;
import com.codepath.edurelate.interfaces.PeopleFragmentInterface;

import org.jetbrains.annotations.NotNull;

public class FriendsFragment extends Fragment {

    public static final String TAG = "FriendsFragment";

    FragmentFriendsBinding binding;
    PeopleFragmentInterface peopleListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
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
        binding = FragmentFriendsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        peopleListener = (PeopleFragmentInterface) getActivity();
        setClickListeners();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setClickListeners() {
        binding.tvNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"NEW FRIEND CLICKED");
                peopleListener.findNewFriend();
            }
        });
    }
}