package com.luyuanyuan.musicplayer.fragment;

import androidx.fragment.app.Fragment;

import com.luyuanyuan.musicplayer.entity.Music;

public abstract class BaseFragment extends Fragment {
    public abstract Music getNextMusic();

    public abstract Music getPreviousMusic();

    public abstract Music getRandomMusic();
}
