package com.codepath.apps.hmtweetsapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.hmtweetsapp.fragments.HomeTimelineFragment;
import com.codepath.apps.hmtweetsapp.fragments.MentionsTimelineFragment;

public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Home", "Mentions" };
    private Context context;

    public TimelineFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new HomeTimelineFragment();
        }
        else if(position == 1) {
            return new MentionsTimelineFragment();
        }
        else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
