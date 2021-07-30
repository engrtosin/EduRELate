package com.codepath.edurelate.fragments;

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
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.CategoryAdapter;
import com.codepath.edurelate.databinding.FragmentAboutUserBinding;
import com.codepath.edurelate.interfaces.ProfileFragmentInterface;
import com.codepath.edurelate.models.Category;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.hootsuite.nachos.validator.ChipifyingNachoValidator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AboutUserFragment extends Fragment implements NewPicDialogFragment.NewPicInterface {

    public static final String TAG = "AboutUserFragment";

    FragmentAboutUserBinding binding;
    View rootView;
    ParseUser user;
    ProfileFragmentInterface mListener;
    CategoryAdapter adapter;
    LinearLayoutManager llManager;
    List<Integer> categoryCodes = new ArrayList<>();
    List<Category> interests = new ArrayList<>();
    List<String> suggestions = new ArrayList<>();
    HashMap<String, Category> categoryMap;
    ArrayAdapter<String> nachoAdapter;

    /* ---------------- CONSTRUCTOR ---------------------- */

    public AboutUserFragment() {
        // Required empty public constructor
    }

    public static AboutUserFragment newInstance(ParseUser user) {
        AboutUserFragment fragment = new AboutUserFragment();
        Bundle args = new Bundle();
        args.putParcelable(User.KEY_USER,user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(User.KEY_USER);
            categoryCodes = User.getInterests(user);
        }
        Log.i(TAG,"in on create");
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutUserBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        adapter = new CategoryAdapter(getContext(),interests);
        binding.rvCategories.setAdapter(adapter);
        llManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        binding.rvCategories.setLayoutManager(llManager);
        categoryMap = new HashMap<>();
        queryAllCategories();
        return rootView;
    }

    private void queryAllCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while querying for categories: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Categories queried successfully. Size: " + objects.size());
                int j = 0;
                for (int i = 0; i < objects.size(); i++) {
                    Category category = objects.get(i);
                    if (categoryCodes.contains(category.getCode())) {
                        interests.add(category);
                        adapter.notifyDataSetChanged();
                        continue;
                    }
                    suggestions.add(category.getTitle());
                    categoryMap.put(suggestions.get(j),category);
                    Log.i(TAG,"Suggestion at " + j + ": " + suggestions.get(j));
                    j += 1;
                }
                setupNachoAdapter();
            }
        });
    }

    private void setupNachoAdapter() {
        nachoAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item,suggestions);
        binding.ntvInterests.setAdapter(nachoAdapter);
        binding.ntvInterests.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
        binding.ntvInterests.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        binding.ntvInterests.setNachoValidator(new ChipifyingNachoValidator());
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener = (ProfileFragmentInterface) getActivity();
        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        setIconVisibilities();

        // TODO: Use User static method.
        ParseFile image = user.getParseFile(User.KEY_USER_PIC);
        Log.i(TAG,"user image: " + image);
        if (image != null) {
            Log.i(TAG,"about to glide user pic");
            Glide.with(this).load(image.getUrl()).into(binding.ivUserPic);
        }
        // TODO: Use User static method.
        binding.tvUsername.setText(user.getUsername());
        binding.tvPassword.setText(user.getString(User.KEY_PASSWORD));
        binding.tvFirstName.setText(User.getFirstName(user));
        binding.tvLastName.setText(User.getLastName(user));
    }

    private void setIconVisibilities() {
        if (User.compareUsers(user,ParseUser.getCurrentUser())) {
            binding.ivEditPic.setVisibility(View.VISIBLE);
            binding.tvDescPassword.setVisibility(View.VISIBLE);
            binding.tvPassword.setVisibility(View.VISIBLE);
            binding.ivChat.setVisibility(View.GONE);
            binding.tvActInvite.setVisibility(View.GONE);
            binding.tvActSendMsg.setVisibility(View.GONE);
        }
    }

    private void setClickListeners() {
        Log.i(TAG,"click listeners to be set");
        binding.tvAddInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rlWholePage.setVisibility(View.GONE);
                binding.rlNewInterest.setVisibility(View.VISIBLE);
            }
        });
        binding.ivDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Chip> chips = binding.ntvInterests.getAllChips();
                addInterests(chips);
                binding.rlWholePage.setVisibility(View.VISIBLE);
                binding.rlNewInterest.setVisibility(View.GONE);
            }
        });
        binding.ivEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPicDialog();
            }
        });
        binding.ivLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });
        binding.ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"On click chat");
                // TODO: Create a helper method
                // I can also query once the user gets into this room.
                ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
                query.include(Member.KEY_USER);
                query.include(Member.KEY_GROUP);
                query.whereEqualTo(Member.KEY_FRIEND,user);
                query.whereContainedIn(Member.KEY_USER, Arrays.asList(ParseUser.getCurrentUser()));
                query.findInBackground(new FindCallback<Member>() {
                    @Override
                    public void done(List<Member> objects, ParseException e) {
                        if (e != null) {
                            Log.e(TAG,"Error while trying to get member object: " + e.getMessage(),e);
                            return;
                        }
                        if (objects.size() != 0) {
                            Log.i(TAG,"Member object found for a chat: ");
                            mListener.goChatActivity(objects.get(0));
                            return;
                        }
                        Log.i(TAG,"Member object not found for a chat: ");
                        Member member = Group.newFriendGroup(ParseUser.getCurrentUser(),user);
                        mListener.goChatActivity(member);
                    }
                });
            }
        });
    }

    private void addInterests(List<Chip> chips) {
        List<Integer> newInterests = new ArrayList<>();
        for (int i = 0; i < chips.size(); i++) {
            String chipTxt = chips.get(i).getText().toString();
            if (suggestions.contains(chipTxt)) {
                interests.add(categoryMap.get(chipTxt));
                newInterests.add(categoryMap.get(chipTxt).getCode());
            }
        }
        adapter.notifyDataSetChanged();
        User.addNewInterests(user,newInterests);
    }

    /* -------------------- new pic methods ---------------------- */
    private void showNewPicDialog() {
        FragmentManager fm = getChildFragmentManager();
        NewPicDialogFragment newPicDialogFragment = NewPicDialogFragment.newInstance("New Pic");
        newPicDialogFragment.show(fm, "fragment_new_pic");
    }

    @Override
    public void picSaved(ParseFile parseFile) {
        User.setUserPic(user,parseFile);
        initializeViews();
    }
}