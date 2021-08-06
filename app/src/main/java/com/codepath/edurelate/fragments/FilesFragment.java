package com.codepath.edurelate.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.R;
import com.codepath.edurelate.activities.GroupDetailsActivity;
import com.codepath.edurelate.databinding.FragmentFilesBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FilesFragment extends Fragment {

    public static final String TAG = "FilesFragment";

    FragmentFilesBinding binding;
    View rootView;
    FilesListener mListener;
    GroupDetailsActivity pActivity;
    File newPdf;

    public interface FilesListener {
        void intentToFiles();
        void pdfResult(File pdfFile);
    }

    public void setListener(FilesListener listener) {
        mListener = listener;
    }

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
        pActivity = (GroupDetailsActivity) getActivity();
        setPDFTransferListener();
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

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClickListeners();
    }

    private void setClickListeners() {
        binding.tvNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.intentToFiles();
            }
        });
    }

    private void setPDFTransferListener() {
        pActivity.setPDFTransferInterface(new FilesListener() {
            @Override
            public void intentToFiles() {

            }

            @Override
            public void pdfResult(File pdfFile) {
                Log.i(TAG,"Receiving file from activity");
                newPdf = pdfFile;
            }
        });
    }


}