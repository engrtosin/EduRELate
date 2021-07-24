package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutUserFragment extends Fragment {

    public AboutUserFragment() {
        // Required empty public constructor
    }

    public static AboutUserFragment newInstance() {
        AboutUserFragment fragment = new AboutUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_user, container, false);
    }
}