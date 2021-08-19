package com.luyuanyuan.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Lyric;
import com.luyuanyuan.musicplayer.ui.LyricListView;

public class LyricFragment extends Fragment {
    private TextView tvTitle;
    private TextView tvArtist;
    private LyricListView mLyricListView;
    private TextView tvEmptyLyricInfo;
    private LinearLayout mLyricContainer;

    private Lyric mLyric;
    private Lyric.Line mLine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lyric, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        updateLyric(mLyric);
        updateLine(mLine);
    }

    private void initViews() {
        View rootView = getView();
        tvTitle = rootView.findViewById(R.id.tvTitle);
        tvArtist = rootView.findViewById(R.id.tvArtist);
        mLyricListView = rootView.findViewById(R.id.lyricListView);
        tvEmptyLyricInfo = rootView.findViewById(R.id.tvEmptyLyricInfo);
        mLyricContainer = rootView.findViewById(R.id.lyricContainer);
    }

    public void updateLyric(Lyric lyric) {
        mLyric = lyric;
        if (getView() == null) {
            return;
        }
        if (mLyric == null) {
            mLyricContainer.setVisibility(View.GONE);
            tvEmptyLyricInfo.setVisibility(View.VISIBLE);
        } else {
            mLyricContainer.setVisibility(View.VISIBLE);
            tvEmptyLyricInfo.setVisibility(View.GONE);

            mLyricListView.setLineList(lyric.getLineList());
            tvTitle.setText(lyric.getTitle());
            tvArtist.setText(lyric.getArtist());
        }
    }

    public void updateLine(Lyric.Line line) {
        mLine = line;
        if (getView() == null || mLine == null) {
            return;
        }
        mLyricListView.setSelectedLine(line.getLinePosition());
    }
}
