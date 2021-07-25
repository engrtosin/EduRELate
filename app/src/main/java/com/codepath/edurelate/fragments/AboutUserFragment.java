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
import com.codepath.edurelate.R;
import com.codepath.edurelate.activities.HomeActivity;
import com.codepath.edurelate.activities.ProfileActivity;
import com.codepath.edurelate.databinding.ActivityProfileBinding;
import com.codepath.edurelate.databinding.FragmentAboutUserBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

public class AboutUserFragment extends Fragment implements NewPicDialogFragment.NewPicInterface {

    public static final String TAG = "AboutUserFragment";

    FragmentAboutUserBinding binding;
    View rootView;
    ParseUser user;
    AboutUserInterface mListener;

    /* ----------------- INTERFACE ----------------------- */
    public interface AboutUserInterface {
        void goLoginActivity();
        void goChatActivity(Member member);
    }

    public void setInterfaceListener(AboutUserInterface mListener)  {
        this.mListener = mListener;
    }

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
        user = savedInstanceState.getParcelable(User.KEY_USER);
        Log.i(TAG,"in on create");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAboutUserBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        binding.ivEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPicDialog();
            }
        });
        binding.ivLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                mListener.goLoginActivity();
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