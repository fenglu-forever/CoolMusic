package com.luyuanyuan.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.activity.MainActivity;
import com.luyuanyuan.musicplayer.adapter.MusicAdapter;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.MusicUtil;

import java.util.List;


public class MusicFragment extends BaseFragment {
    private ListView mMusicList;
    private MusicAdapter mAdapter;
    private int mSelectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();
        initAdapters();
    }

    private void initViews() {
        View rootView = getView();
        mMusicList = rootView.findViewById(R.id.music_list);
    }

    private void initListeners() {
    mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            List<Music> musicList = mAdapter.getMusicList();
            for (Music music : musicList) {
                music.setSelected(false);
            }
            mSelectedPosition = position;
            Music selectedMusic = mAdapter.getItem(position);
            selectedMusic.setSelected(true);
            mAdapter.notifyDataSetChanged();
            MainActivity activity = (MainActivity) getActivity();
            activity.requestPlayMusic(selectedMusic);
        }
    });
}

    private void initAdapters() {
        mAdapter = new MusicAdapter(getActivity(), MusicUtil.getMusicList());
        mMusicList.setAdapter(mAdapter);
    }

    @Override
    public Music getNextMusic() {
        mSelectedPosition++;
        List<Music> musicList = mAdapter.getMusicList();
        if (mSelectedPosition >= musicList.size()) {
            mSelectedPosition = 0;
        }
        // 为了让音乐列表刷新到next的选中状态
        for (Music music : musicList) {
            music.setSelected(false);
        }
        Music nexMusic = musicList.get(mSelectedPosition);
        nexMusic.setSelected(true);
        mAdapter.notifyDataSetChanged();
        //
        return nexMusic;
    }
}
