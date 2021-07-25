package com.codepath.edurelate.adapters;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codepath.edurelate.fragments.AboutUserFragment;
import com.codepath.edurelate.fragments.FriendsFragment;
import com.codepath.edurelate.fragments.GroupsFragment;
import com.parse.Parse;
import com.parse.ParseUser;

public class ProfileFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final String TAG = "ProfileFragmentPagerAdapter";

    ParseUser user;

    public interface ProfileFragPagerAdapterInterface {
        public void newFriendsFragment(FriendsFragment friendsFragment);
        public void newGroupsFragment(GroupsFragment groupsFragment);
    }

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "About", "Groups" };
    private Context context;
    private ProfileFragPagerAdapterInterface mListener;

    public ProfileFragmentPagerAdapter(FragmentManager fm, Context context, ParseUser user) {
        super(fm);
        this.context = context;
        this.user = user;
    }

    public void setListener(ProfileFragPagerAdapterInterface mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Log.i(TAG,"" + position);
            AboutUserFragment aboutUserFragment = AboutUserFragment.newInstance(user);
            return aboutUserFragment;
        }
        else if (position == 1) {
            // TODO: remove this else statement
            Log.i(TAG,"" + position);
            GroupsFragment groupsFragment = GroupsFragment.newInstance();
            return groupsFragment;
        }
        return GroupsFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
