package com.dartmouth.yuanjiang.myruns_2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class ActionTabsViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public static final int START = 0;
    public static final int HISTORY = 1;
    public static final int SETTINGS = 2;
    public static final String UI_TAB_START = "START";
    public static final String UI_TAB_HISTORY = "HISTORY";
    public static final String UI_TAB_SETTINGS = "SETTINGS";

    public ActionTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    public Fragment getItem(int pos){
        return fragments.get(pos);
    }

    public int getCount(){
        return fragments.size();
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case START:
                return UI_TAB_START;
            case HISTORY:
                return UI_TAB_HISTORY;
            case SETTINGS:
                return UI_TAB_SETTINGS;
            default:
                break;
        }
        return null;
    }
}
