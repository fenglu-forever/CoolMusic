package com.luyuanyuan.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.adapter.MusicAdapter;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.MusicUtil;

import java.util.List;


public class MusicFragment extends Fragment {
    private ListView mMusicList;
    private MusicAdapter mAdapter;

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
            Music selectedMusic = mAdapter.getItem(position);
            selectedMusic.setSelected(true);
            mAdapter.notifyDataSetChanged();
        }
    });
    }

    private void initAdapters() {
        mAdapter = new MusicAdapter(getActivity(), MusicUtil.getMusicList());
        mMusicList.setAdapter(mAdapter);
    }
}
