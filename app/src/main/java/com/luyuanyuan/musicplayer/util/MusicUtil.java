package com.luyuanyuan.musicplayer.util;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.luyuanyuan.musicplayer.app.MusicPlayerApp;
import com.luyuanyuan.musicplayer.entity.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicUtil {
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    public static List<Music> getMusicList() {
        List<Music> musicList = new ArrayList<>();
        Cursor cursor = MusicPlayerApp.getAppContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                if (isMusic == 1) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    name = name.replace(".mp3", "");
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    int size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    Music music = new Music();
                    music.setName(name);
                    music.setId(id);
                    music.setArtist(artist);
                    music.setUrl(url);
                    music.setDuration(duration);
                    music.setSize(size);
                    music.setAlbumId(albumId);
                    music.setAlbumName(albumName);
                    musicList.add(music);
                }
            }
            cursor.close();
        }
        return musicList;
    }

    public static Uri getAlbumPicUri(long album_id) {
        return ContentUris.withAppendedId(albumArtUri, album_id);
    }

    public static String getMusicDuration(int duration) {
        int sec = duration / 1000;
        int min = 0;
        if (sec >= 60) {
            min = sec / 60;
        }
        sec = sec - min * 60;
        String minuteStr;
        String secondStr;
        if (min < 10) {
            minuteStr = "0" + min;
        } else {
            minuteStr = min + "";
        }
        if (sec < 10) {
            secondStr = "0" + sec;
        } else {
            secondStr = sec + "";
        }
        return minuteStr + ":" + secondStr;
    }
}




