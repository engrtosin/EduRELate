package com.codepath.edurelate.adapters;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codepath.edurelate.fragments.FriendsFragment;
import com.codepath.edurelate.fragments.GroupsFragment;

public class PeopleFragmentPagerAdapter  extends FragmentPagerAdapter {

    public interface PeopleFragPagerAdapterInterface {
        public void newFriendsFragment(FriendsFragment friendsFragment);
        public void newGroupsFragment(GroupsFragment groupsFragment);
    }

    public static final String TAG = "PeopleFragmentPagerAdapter";

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Friends", "Groups" };
    private Context context;
    private PeopleFragPagerAdapterInterface mListener;

    public PeopleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public void setListener(PeopleFragPagerAdapterInterface mListener) {
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
            FriendsFragment friendsFragment = FriendsFragment.newInstance();
            return friendsFragment;
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
