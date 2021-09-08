package com.luyuanyuan.musicplayer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.card.MaterialCardView;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.activity.MusicDetailActivity;
import com.luyuanyuan.musicplayer.entity.Lyric;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.ui.LyricTextView;
import com.luyuanyuan.musicplayer.util.Constant;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.PreferenceUtil;
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
    private LyricTextView mLyricTextView;
    private int mProgress;
    private int mCurrentDuration;
    private SeekBar mSeekBar;
    private ImageView ivPlayMode;
    private ImageView ivCollect;
    private boolean isTouchSeekBar;
    private int[] mPlayModeArray = {Constant.PLAY_MODE_SEQUENCE, Constant.PLAY_MODE_SINGLE, Constant.PLAY_MODE_RANDOM};
    private Lyric mLyric;
    private Lyric.Line mLine;
    private int mPlayingPosition;

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
        updateLyric(mLyric);
        updateLine(mLine);
        updatePlayingPosition(mPlayingPosition);
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
        ivPlayMode = rootView.findViewById(R.id.ivPlayMode);
        setPlayMode(PreferenceUtil.getInt(Constant.PREF_KEY_PLAY_MODE, Constant.PLAY_MODE_SEQUENCE));
        ivCollect = rootView.findViewById(R.id.ivCollect);
        mLyricTextView = rootView.findViewById(R.id.lyricTextView);
    }

    private void setPlayMode(int playMode) {
        switch (playMode) {
            case Constant.PLAY_MODE_SEQUENCE:
                ivPlayMode.setImageResource(R.drawable.ic_music_detail_play_mode_sequence);
                break;
            case Constant.PLAY_MODE_SINGLE:
                ivPlayMode.setImageResource(R.drawable.ic_music_detail_play_mode_single);
                break;
            case Constant.PLAY_MODE_RANDOM:
                ivPlayMode.setImageResource(R.drawable.ic_music_detail_play_mode_random);
                break;
            default:
                break;
        }
    }

    private void initListeners() {
        btnPlayerOrPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        ivPlayMode.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                if (mSelectedMusic != null) {
                    mProgress = progress;
                    mCurrentDuration = mSelectedMusic.getDuration() * mProgress / 100;
                    tvCurrentDuration.setText(MusicUtil.getMusicDuration(mCurrentDuration));
                    MusicDetailActivity detailActivity = (MusicDetailActivity) getActivity();
                    detailActivity.updateLine(mCurrentDuration);
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
        ivCollect.setOnClickListener(this);
    }

    public void updateSelectedMusic(Music selectedMusic) {
        mSelectedMusic = selectedMusic;
        if (mSelectedMusic == null || getView() == null) {
            return;
        }
        Glide.with(this)
                .asBitmap()
                .load(MusicUtil.getAlbumPicUri(mSelectedMusic.getAlbumId()))
                .error(R.drawable.ic_default_detail_music_pic)
                .into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mCoverImg.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        mCoverImg.setImageResource(R.drawable.ic_default_detail_music_pic);
                    }
                });
        tvName.setText(mSelectedMusic.getName());
        tvArtist.setText(mSelectedMusic.getArtist());
        if (mSelectedMusic.isPlaying()) {
            btnPlayerOrPause.setImageResource(R.drawable.ic_music_detail_play);
        } else {
            btnPlayerOrPause.setImageResource(R.drawable.ic_music_detail_pause);
        }
        tvTotalDuration.setText(MusicUtil.getMusicDuration(mSelectedMusic.getDuration()));
        updateCollectIcon();
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

    public void updateLyric(Lyric lyric) {
        mLyric = lyric;
        if (getView() == null) {
            return;
        }
        if (mLyric == null) {
            mLyricTextView.setLyricLine(null);
        }
    }

    public void updateLine(Lyric.Line line) {
        mLine = line;
        if (mLine == null || getView() == null || TextUtils.isEmpty(line.getText())) {
            return;
        }
        mLyricTextView.setLyricLine(mLine);
    }

    public void updatePlayingPosition(int playingPosition) {
        mPlayingPosition = playingPosition;
        if (getView() == null) {
            return;
        }
        mLyricTextView.setPlayingDuration(mPlayingPosition);
    }

    public void updateCollectIcon() {
        if (getView() == null || mSelectedMusic == null) {
            return;
        }
        ivCollect.setSelected(MusicUtil.isCollect(mSelectedMusic));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlayOrPause:
                Intent intentPlayOrPause = new Intent(Constant.ACTION_PLAY_OR_PAUSE_MUSIC);
                getActivity().sendBroadcast(intentPlayOrPause);
                break;
            case R.id.btnNext:
                Intent intent = new Intent(Constant.ACTION_NEXT_MUSIC);
                getActivity().sendBroadcast(intent);
                break;
            case R.id.btnPrevious:
                Intent intentPrevious = new Intent(Constant.ACTION_PREVIOUS_MUSIC);
                getActivity().sendBroadcast(intentPrevious);
                break;
            case R.id.ivPlayMode:
                int playMode = PreferenceUtil.getInt(Constant.PREF_KEY_PLAY_MODE, Constant.PLAY_MODE_SEQUENCE);
                int index = 0;
                for (int i = 0; i < mPlayModeArray.length; i++) {
                    if (mPlayModeArray[i] == playMode) {
                        index = i;
                        break;
                    }
                }
                index++;
                if (index >= mPlayModeArray.length) {
                    index = 0;
                }
                playMode = mPlayModeArray[index];
                PreferenceUtil.putInt(Constant.PREF_KEY_PLAY_MODE, playMode);
                setPlayMode(playMode);
                break;
            case R.id.ivCollect:
                Intent collectIntent = new Intent(Constant.ACTION_UPDATE_MUSIC_COLLECT_STATE);
                getActivity().sendBroadcast(collectIntent);
                break;
            default:
                break;
        }
    }
}
