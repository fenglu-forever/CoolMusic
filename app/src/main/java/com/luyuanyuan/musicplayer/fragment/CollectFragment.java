package com.luyuanyuan.musicplayer.fragment;

import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.MusicUtil;

import java.util.List;

public class CollectFragment extends MusicListFragment {
    @Override
    public List<Music> loadMusicList() {
        return MusicUtil.getCollectMusicList();
    }
}
