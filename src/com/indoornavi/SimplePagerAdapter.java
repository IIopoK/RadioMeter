package com.indoornavi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

class SimplePagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments = new ArrayList<Fragment>();

    public SimplePagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new SignalRecorderPage());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return TestFragmentAdapter.CONTENT[position % CONTENT.length];
//    }
}