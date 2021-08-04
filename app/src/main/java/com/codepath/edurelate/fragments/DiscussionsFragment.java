package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.FragmentDiscussionsBinding;

public class DiscussionsFragment extends Fragment {

    public static final String TAG = "DiscussionsFragment";

    FragmentDiscussionsBinding binding;
    View rootView;

    public DiscussionsFragment() {
        // Required empty public constructor
    }

    public static DiscussionsFragment newInstance() {
        DiscussionsFragment fragment = new DiscussionsFragment();
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
        binding = FragmentDiscussionsBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }
}