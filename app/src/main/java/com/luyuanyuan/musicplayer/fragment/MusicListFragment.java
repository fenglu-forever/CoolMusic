package com.luyuanyuan.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.activity.MainActivity;
import com.luyuanyuan.musicplayer.adapter.MusicAdapter;
import com.luyuanyuan.musicplayer.entity.Music;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MusicListFragment extends BaseFragment {
    private ListView mMusicList;
    private TextView tvMusicNumber;
    private MusicAdapter mAdapter;
    private int mSelectedPosition = -1;
    private Random mRandom = new Random();
    private List<Music> mMusicData = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicData.clear();
        mMusicData.addAll(loadMusicList());
    }

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
        tvMusicNumber = rootView.findViewById(R.id.tvMusicNumber);
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
                activity.playMusic(selectedMusic);
            }
        });
    }

    private void initAdapters() {
        List<Music> musicList = mMusicData;
        MainActivity activity = (MainActivity) getActivity();
        Music selMusic = activity.getSelectedMusic();
        if (selMusic != null) {
            for (Music music : musicList) {
                if (music.getId() == selMusic.getId()) {
                    music.setSelected(selMusic.isSelected());
                    music.setPlaying(selMusic.isPlaying());
                    break;
                }
            }
        }
        mAdapter = new MusicAdapter(getActivity(), musicList);
        mMusicList.setAdapter(mAdapter);
        tvMusicNumber.setText(mAdapter.getCount() + "首");
    }

    @Override
    public Music getNextMusic() {
        List<Music> musicList = mAdapter.getMusicList();
        Music nextMusic = null;
        if (musicList.size() > 0) {
            mSelectedPosition++;
            if (mSelectedPosition >= musicList.size()) {
                mSelectedPosition = 0;
            }
            // 为了让音乐列表刷新到next的选中状态
            for (Music music : musicList) {
                music.setSelected(false);
            }
            nextMusic = musicList.get(mSelectedPosition);
            nextMusic.setSelected(true);
            mAdapter.notifyDataSetChanged();
            mMusicList.smoothScrollToPosition(mSelectedPosition);
        }
        return nextMusic;
    }

    @Override
    public Music getPreviousMusic() {
        List<Music> musicList = mAdapter.getMusicList();
        Music previousMusic = null;
        if (musicList.size() > 0) {
            mSelectedPosition--;
            if (mSelectedPosition < 0) {
                mSelectedPosition = musicList.size() - 1;
            }
            for (Music music : musicList) {
                music.setSelected(false);
            }
            previousMusic = musicList.get(mSelectedPosition);
            previousMusic.setSelected(true);
            mAdapter.notifyDataSetChanged();
            mMusicList.smoothScrollToPosition(mSelectedPosition);
        }
        return previousMusic;
    }

    @Override
    public Music getRandomMusic() {
        List<Music> musicList = mAdapter.getMusicList();
        Music randomMusic = null;
        if (musicList.size() > 0) {
            int randomPosition = mRandom.nextInt(musicList.size());
            // 保证只有一个处于选中状态
            for (Music music : musicList) {
                music.setSelected(false);
            }
            mSelectedPosition = randomPosition;
            randomMusic = musicList.get(randomPosition);
            randomMusic.setSelected(true);
            mAdapter.notifyDataSetChanged();
            mMusicList.smoothScrollToPosition(mSelectedPosition);
        }
        return randomMusic;
    }

    public abstract List<Music> loadMusicList();

    public void reloadMusicListAndRefresh() {
        mMusicData.clear();
        mMusicData.addAll(loadMusicList());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
