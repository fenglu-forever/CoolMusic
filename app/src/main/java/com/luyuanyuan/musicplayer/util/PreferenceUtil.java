package com.luyuanyuan.musicplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.luyuanyuan.musicplayer.app.MusicPlayerApp;

/**
 * add your description
 *
 * @author fenglu
 * @since xx-xx-xx
 */
public class PreferenceUtil {
    private static final String PREFERENCE_FILE_NAME = "music_app_prefs";
    private static SharedPreferences mPreferences = MusicPlayerApp.getAppContext().getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);


    public static void putString(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }
}
