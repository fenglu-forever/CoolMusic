package com.luyuanyuan.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.luyuanyuan.musicplayer.R;
import com.luyuanyuan.musicplayer.entity.Music;
import com.luyuanyuan.musicplayer.util.MusicUtil;
import com.luyuanyuan.musicplayer.util.UiUtil;

import java.util.List;

public class MusicManagerAdapter extends BaseAdapter {
    private Context mContext;
    private List<Music> mMusicList;
    private LayoutInflater mInflater;

    public MusicManagerAdapter(Context context, List<Music> musicList) {
        mContext = context;
        mMusicList = musicList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Music getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Music music = mMusicList.get(position);
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.item_music_list_manger, parent, false);
            holder = new ViewHolder();
            holder.ivPic = view.findViewById(R.id.ivPic);
            UiUtil.roundView(holder.ivPic, mContext.getResources().getDimension(R.dimen.music_album_pic_conner));
            holder.tvName = view.findViewById(R.id.tvName);
            holder.tvArtist = view.findViewById(R.id.tvArtist);
            holder.ivSelected = view.findViewById(R.id.ivSelected);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.tvName.setText(music.getName());
        holder.tvArtist.setText(music.getArtist());
        Glide.with(mContext)
                .load(MusicUtil.getAlbumPicUri(music.getAlbumId()))
                .placeholder(R.drawable.ic_default_music_album_pic)
                .error(R.drawable.ic_default_music_album_pic)
                .into(holder.ivPic);
        if (music.isSelected()) {
            holder.ivSelected.setImageResource(R.drawable.ic_check);
        } else {
            holder.ivSelected.setImageResource(R.drawable.ic_not_check);
        }
        return view;
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

    static class ViewHolder {
        private ImageView ivPic;
        private TextView tvName;
        private TextView tvArtist;
        private ImageView ivSelected;
    }
}
