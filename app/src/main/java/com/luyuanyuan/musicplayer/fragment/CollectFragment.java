package com.luyuanyuan.musicplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.activity.MusicMangerActivity;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.MusicUtil;

import java.util.List;

public class CollectFragment extends MusicListFragment {
    @Override
    public List<Music> loadMusicList() {
        return MusicUtil.getCollectMusicList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.ivManagerList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MusicMangerActivity.class);
                startActivity(intent);
            }
        });
    }
}
