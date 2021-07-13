package com.luyuanyuan.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.UiUtil;

public class SongFragment extends Fragment {
    private Music mSelectedMusic;
    private MaterialCardView mCoverCard;
    private ImageView mCoverImg;
    private TextView tvName;
    private TextView tvArtist;

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
    }

    private void initListeners() {

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
    }
}
