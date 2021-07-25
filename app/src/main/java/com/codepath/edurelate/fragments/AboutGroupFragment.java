package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.databinding.FragmentAboutGroupBinding;
import com.codepath.edurelate.interfaces.GroupDetailsInterface;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class AboutGroupFragment extends Fragment implements NewPicDialogFragment.NewPicInterface {

    public static final String TAG = "AboutGroupFragment";

    FragmentAboutGroupBinding binding;
    View rootView;
    Member member;
    Group group;
    GroupDetailsInterface mListener;


    public AboutGroupFragment() {
        // Required empty public constructor
    }

    public static AboutGroupFragment newInstance(Member member) {
        AboutGroupFragment fragment = new AboutGroupFragment();
        Bundle args = new Bundle();
        args.putParcelable(Member.KEY_MEMBER,member);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            member = getArguments().getParcelable(Member.KEY_MEMBER);
            group = member.getGroup();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutGroupBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener = (GroupDetailsInterface) getActivity();
        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        Log.i(TAG,"Initializing views in " + TAG);
        setIconVisibility();
        initializeGroupSection();
        initializeOwnerSection();
    }

    private void initializeGroupSection() {
        ParseFile image = group.getGroupPic();
        if (image != null) {
            Log.i(TAG,"about to glide curr user pic");
            Glide.with(this).load(image.getUrl()).into(binding.ivGroupPic);
        }
        Log.i(TAG,"Created on: " + group.getCreatedAt());
    }

    private void initializeOwnerSection() {
        ParseFile image;
        if (group.has(Group.KEY_GROUP_PIC)) {
            image = group.getOwner().getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Log.i(TAG,"about to glide owner pic");
                Glide.with(this).load(image.getUrl()).into(binding.ivOwnerPic);
            }
        }
        binding.tvOwnerName.setText(User.getFullName(group.getOwner()));
    }

    private void setIconVisibility() {
        if (User.compareUsers(group.getOwner(),ParseUser.getCurrentUser())) {
            binding.ivOwnerChat.setVisibility(View.INVISIBLE);
            binding.ivEditPic.setVisibility(View.VISIBLE);
            return;
        }
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");

        binding.tvLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.leave();
            }
        });
        binding.tvActInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.inviteUser();
            }
        });
        binding.ivEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPicDialog();
            }
        });
        binding.ivGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.ivOwnerChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.chatWithUser(group.getOwner());
            }
        });
        binding.cvOwnerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goProfile(group.getOwner());
            }
        });
        binding.tvOwnerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goProfile(group.getOwner());
            }
        });
    }

    /* -------------------- NEW PIC METHODS ---------------------- */
    private void showNewPicDialog() {
        FragmentManager fm = getChildFragmentManager();
        NewPicDialogFragment newPicDialogFragment = NewPicDialogFragment.newInstance("New Pic");
        newPicDialogFragment.show(fm, "fragment_new_pic");
    }

    @Override
    public void picSaved(ParseFile parseFile) {
        group.setGroupPic(parseFile);
    }
}