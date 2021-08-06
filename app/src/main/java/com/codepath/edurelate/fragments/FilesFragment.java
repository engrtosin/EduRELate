package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.FragmentFilesBinding;

public class FilesFragment extends Fragment {

    public static final String TAG = "FilesFragment";

    FragmentFilesBinding binding;
    View rootView;

    public FilesFragment() {
        // Required empty public constructor
    }

    public static FilesFragment newInstance() {
        FilesFragment fragment = new FilesFragment();
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
        binding = FragmentFilesBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }
}