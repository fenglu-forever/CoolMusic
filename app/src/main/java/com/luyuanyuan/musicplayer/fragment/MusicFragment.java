package com.luyuanyuan.musicplayer.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.MusicUtil;

import java.util.List;

public class MusicFragment extends MusicListFragment {
    @Override
    public List<Music> loadMusicList() {
        return MusicUtil.getMusicList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.ivManagerList).setVisibility(View.GONE);
    }
}
