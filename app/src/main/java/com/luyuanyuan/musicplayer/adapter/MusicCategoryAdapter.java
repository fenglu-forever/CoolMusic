package com.luyuanyuan.musicplayer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MusicCategoryAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;

    public MusicCategoryAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> fragments) {
        super(fm, behavior);
        mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
