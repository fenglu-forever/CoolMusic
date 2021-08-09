package com.luyuanyuan.musicplayer.util;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.luyuanyuan.musicplayer.app.MusicPlayerApp;

public class BroadcastUtil {
    private static final LocalBroadcastManager MANGER = LocalBroadcastManager.getInstance(MusicPlayerApp.getAppContext());

    public static void postBroadcast(Intent intent) {
        MANGER.sendBroadcast(intent);
    }

    public static void subscribeBroadcast(BroadcastReceiver receiver, IntentFilter filter) {
        MANGER.registerReceiver(receiver, filter);
    }

    public static void unsubscribeBroadcast(BroadcastReceiver receiver) {
        MANGER.unregisterReceiver(receiver);
    }
}
