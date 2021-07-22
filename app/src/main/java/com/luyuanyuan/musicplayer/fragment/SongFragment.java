package com.luyuanyuan.musicplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.Constant;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.UiUtil;

public class SongFragment extends Fragment implements View.OnClickListener {
    private Music mSelectedMusic;
    private MaterialCardView mCoverCard;
    private ImageView mCoverImg;
    private TextView tvName;
    private TextView tvArtist;
    private ImageView btnPlayerOrPause;
    private ImageView btnPrevious;
    private ImageView btnNext;
    private TextView tvCurrentDuration;
    private TextView tvTotalDuration;
    private int mProgress;
    private int mCurrentDuration;
    private SeekBar mSeekBar;
    private boolean isTouchSeekBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();
        updateSelectedMusic(mSelectedMusic);
        updateMusicProgress(mProgress, mCurrentDuration);
    }

    private void initViews() {
        View rootView = getView();
        mCoverCard = rootView.findViewById(R.id.coverCard);
        mCoverImg = rootView.findViewById(R.id.coverImg);
        ViewGroup.LayoutParams lpCard = mCoverCard.getLayoutParams();
        int cardSize = UiUtil.getScreenInfo(getActivity()).widthPixels - getResources().getDimensionPixelSize(R.dimen.dp_48);
        lpCard.width = cardSize;
        lpCard.height = cardSize;
        mCoverCard.setLayoutParams(lpCard);
        tvName = rootView.findViewById(R.id.tvName);
        tvArtist = rootView.findViewById(R.id.tvArtist);
        btnPlayerOrPause = rootView.findViewById(R.id.btnPlayOrPause);
        btnPrevious = rootView.findViewById(R.id.btnPrevious);
        btnNext = rootView.findViewById(R.id.btnNext);
        tvCurrentDuration = rootView.findViewById(R.id.tvCurrentDuration);
        tvTotalDuration = rootView.findViewById(R.id.tvTotalDuration);
        mSeekBar = rootView.findViewById(R.id.sekbar);
    }

    private void initListeners() {
        btnPlayerOrPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mSelectedMusic != null) {
                    mProgress = progress;
                    mCurrentDuration = mSelectedMusic.getDuration() * mProgress / 100;
                    tvCurrentDuration.setText(MusicUtil.getMusicDuration(mCurrentDuration));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouchSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouchSeekBar = false;
                Intent intent = new Intent(Constant.ACTION_SEEK_MUSIC);
                intent.putExtra(Constant.EXTRA_MUSIC_CURRENT_DURATION, mCurrentDuration);
                getActivity().sendBroadcast(intent);
            }
        });
    }

    public void updateSelectedMusic(Music selectedMusic) {
        mSelectedMusic = selectedMusic;
        if (mSelectedMusic == null || getView() == null) {
            return;
        }
        Glide.with(this)
                .load(MusicUtil.getAlbumPicUri(mSelectedMusic.getAlbumId()))
                .placeholder(R.drawable.ic_default_music_album_pic)
                .error(R.drawable.ic_default_music_album_pic)
                .into(mCoverImg);
        tvName.setText(mSelectedMusic.getName());
        tvArtist.setText(mSelectedMusic.getArtist());
        if (mSelectedMusic.isPlaying()) {
            btnPlayerOrPause.setImageResource(R.drawable.ic_music_detail_play);
        } else {
            btnPlayerOrPause.setImageResource(R.drawable.ic_music_detail_pause);
        }
        tvTotalDuration.setText(MusicUtil.getMusicDuration(mSelectedMusic.getDuration()));
    }

    public void updateMusicProgress(int progress, int currentDuration) {
        if (isTouchSeekBar) {
            return;
        }
        mProgress = progress;
        mCurrentDuration = currentDuration;
        if (getView() == null || mSelectedMusic == null) {
            return;
        }
        mSeekBar.setProgress(progress);
        tvCurrentDuration.setText(MusicUtil.getMusicDuration(currentDuration));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlayOrPause:
                if (mSelectedMusic != null) {
                    Intent intent;
                    if (mSelectedMusic.isPlaying()) {
                        intent = new Intent(Constant.ACTION_PAUSE_MUSIC);
                    } else {
                        intent = new Intent(Constant.ACTION_PLAY_MUSIC);
                    }
                    getActivity().sendBroadcast(intent);
                }
                break;
            case R.id.btnNext:
                Intent intent = new Intent(Constant.ACTION_NEXT_MUSIC);
                getActivity().sendBroadcast(intent);
                break;
            default:
                break;
        }
    }
}
