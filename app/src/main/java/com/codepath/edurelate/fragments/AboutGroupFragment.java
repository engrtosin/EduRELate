package com.codepath.edurelate.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.CategoryAdapter;
import com.codepath.edurelate.databinding.FragmentAboutGroupBinding;
import com.codepath.edurelate.databinding.ItemCategoryBinding;
import com.codepath.edurelate.interfaces.GroupDetailsInterface;
import com.codepath.edurelate.models.Category;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class AboutGroupFragment extends Fragment implements NewPicDialogFragment.NewPicInterface {

    public static final String TAG = "AboutGroupFragment";

    FragmentAboutGroupBinding binding;
    View rootView;
    Member member;
    Group group;
    List<Category> categories;
    GroupDetailsInterface mListener;
    CategoryAdapter adapter;
    LinearLayoutManager llManager;

    public AboutGroupFragment() {
        // Required empty public constructor
    }

    public static AboutGroupFragment newInstance(Member member,Group group) {
        AboutGroupFragment fragment = new AboutGroupFragment();
        Bundle args = new Bundle();
        args.putParcelable(Group.KEY_GROUP,group);
        args.putParcelable(Member.KEY_MEMBER,member);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = getArguments().getParcelable(Group.KEY_GROUP);
            categories = group.getCategories();
            member = getArguments().getParcelable(Member.KEY_MEMBER);
            if (member == null) {

            }
            Log.i(TAG,"Categories is " + categories.size());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutGroupBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        adapter = new CategoryAdapter(getContext(),categories);
        binding.rvCategories.setAdapter(adapter);
        llManager = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        binding.rvCategories.setLayoutManager(llManager);
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
        String createdDate = getDate(group.getCreatedAt());
        binding.tvCreatedAt.setText(createdDate);
        if (User.compareUsers(ParseUser.getCurrentUser(),group.getOwner())) {
            binding.ivEditPic.setVisibility(View.VISIBLE);
        }
    }

    private String getDate(Date createdAt) {
        String[] dateList = createdAt.toString().split(" ");
        return "Created on " + dateList[0] + " " + dateList[1] + " " + dateList[2] + ", " + dateList[5];
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
            binding.ivOwnerChat.setVisibility(View.GONE);
            binding.ivEditPic.setVisibility(View.VISIBLE);
            binding.tvActInvite.setVisibility(View.VISIBLE);
            binding.tvLeave.setVisibility(View.GONE);
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
        binding.ivEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPicDialog();
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