package com.codepath.edurelate.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.FragmentGroupsBinding;
import com.codepath.edurelate.databinding.FragmentNewGroupBinding;
import com.codepath.edurelate.models.Group;

public class NewGroupDialogFragment extends DialogFragment  implements TextView.OnEditorActionListener {

    public static final String TAG = "NewGroupDialogFragment";

    FragmentNewGroupBinding binding;

    /* ---------------------- interface ---------------------- */

    public NewGroupDialogFragment() {
    }

    public static NewGroupDialogFragment newInstance(String title) {
        NewGroupDialogFragment frag = new NewGroupDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewGroupBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title", "Enter Name");
        // Show soft keyboard automatically and request focus to field
        binding.etGroupName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setOnClickListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void setOnClickListeners() {
        binding.ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGroup();
            }
        });
    }

    protected void createNewGroup() {
        String groupName = binding.etGroupName.getText().toString();
        if (groupName == null) {
            Toast.makeText(getContext(),"Group name cannot be empty.",Toast.LENGTH_SHORT).show();
            return;
        }
        Group.newGroup(groupName);
        dismiss();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }
}