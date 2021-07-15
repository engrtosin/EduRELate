package com.codepath.edurelate.adapters;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codepath.edurelate.fragments.FriendsFragment;
import com.codepath.edurelate.fragments.GroupsFragment;

public class PeopleFragmentPagerAdapter  extends FragmentPagerAdapter {

    public static final String TAG = "PeopleFragmentPagerAdapter";

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Friends", "Groups" };
    private Context context;

    public PeopleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Log.i(TAG,"" + position);
            return FriendsFragment.newInstance();
        }
        else if (position == 1) {
            // TODO: remove this else statement
            Log.i(TAG,"" + position);
            return GroupsFragment.newInstance();
        }
        return GroupsFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
