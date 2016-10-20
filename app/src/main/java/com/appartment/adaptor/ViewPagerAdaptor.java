package com.appartment.adaptor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by ripulchhabra on 12/10/16.
 */
public class ViewPagerAdaptor extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();

    public ViewPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    public void addFragments(Fragment fragment , String title) {
        this.fragments.add(fragment);
        this.tabTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.tabTitles.get(position);
    }
}
