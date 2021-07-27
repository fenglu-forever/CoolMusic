package com.luyuanyuan.musicplayer.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MusicDataOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "music.db";
    private static final int VERSION = 1;

    private static final String CREATE_MUSIC = "create table Music ("
            + "id integer primary key autoincrement,"
            + "mediaId integer unique,"
            + "name text,"
            + "artist text,"
            + "url text,"
            + "duration integer,"
            + "size integer,"
            + "albumId integer,"
            + "albumName text)";

    public MusicDataOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MUSIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}