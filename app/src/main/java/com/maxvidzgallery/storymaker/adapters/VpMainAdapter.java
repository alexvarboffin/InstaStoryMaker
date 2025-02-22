package com.maxvidzgallery.storymaker.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class VpMainAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> mFragments;

    public VpMainAdapter(FragmentManager fragmentManager, ArrayList<Fragment> arrayList) {
        super(fragmentManager);
        this.mFragments = arrayList;

    }

    public int getCount() {
        return this.mFragments.size();
    }

    public CharSequence getPageTitle(int i) {
        return ((Fragment) this.mFragments.get(i)).getTag();
    }

    public Fragment getItem(int i) {
        return (Fragment) this.mFragments.get(i);
    }
}
