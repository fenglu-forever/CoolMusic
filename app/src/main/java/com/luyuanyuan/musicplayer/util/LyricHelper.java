package com.luyuanyuan.musicplayer.util;

import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luyuanyuan.musicplayer.entity.Lyric;
import com.luyuanyuan.musicplayer.entity.Music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricHelper {
    private static final String TAG = "LyricHelper";
    private static final String LYRIC_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    //private static final String LYRIC_DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "cool_music_lyrics").getPath();

    private static final Pattern PATTERN = Pattern.compile(
            "\\[\\d{1,2}:\\d{1,2}([.:]\\d{1,2})?]");
    private List<File> mLyricList = new ArrayList<>();
    private Handler mHandler = new Handler();
    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            update();
        }
    };
    private int mLineNumber = -1;

    private Lyric mLyric;
    private Callback mCallback;

    public LyricHelper() {
        initLyricList();
    }

    private void initLyricList() {
        File dir = new File(LYRIC_DIR);
        Log.d(TAG, "dir " + dir.getPath());
        if (!dir.exists()) {
            return;
        }
        File[] fileList = dir.listFiles();
        if (fileList == null || fileList.length == 0) {
            return;
        }
        for (File file : fileList) {
            if (file.getName().endsWith(".lrc")) {
                mLyricList.add(file);
            }
        }
    }

    private String getLyricPath(Music music) {
        for (File file : mLyricList) {
            String fileName = file.getName();
            if (fileName.endsWith(".lrc")) {
                fileName = fileName.replace(".lrc", "");
                if (fileName.equals(music.getName())) {
                    return file.getPath();
                }
            }
        }
        return null;
    }

    public void setMusic(Music music) {
        mLyric = null;
        mLineNumber = -1;
        String path = getLyricPath(music);
        Log.d(TAG, "music = " + music.getName() + " path = " + path);
        stop();
        if (path == null) {
            if (mCallback != null) {
                mCallback.onLyricChange(null);
            }
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            if (mCallback != null) {
                mCallback.onLyricChange(null);
            }
            return;
        }
        Lyric lyric = new Lyric();
        List<Lyric.Line> lineList = lyric.getLineList();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
            String lineResult;
            while ((lineResult = reader.readLine()) != null) {
                Log.d(TAG, lineResult);
                if (lineResult.contains("[ti:")) {
                    int start = lineResult.indexOf(":") + 1;
                    int end = lineResult.length() - 1;
                    lyric.setTitle(lineResult.substring(start, end));
                } else if (lineResult.contains("[ar:")) {
                    int start = lineResult.indexOf(":") + 1;
                    int end = lineResult.length() - 1;
                    lyric.setArtist(lineResult.substring(start, end));
                } else if (lineResult.contains("[al:")) {
                    int start = lineResult.indexOf(":") + 1;
                    int end = lineResult.length() - 1;
                    lyric.setAlbum(lineResult.substring(start, end));
                } else {
                    Matcher matcher = PATTERN.matcher(lineResult);
                    if (matcher.find()) {
                        String timeStr = matcher.group();
                        Lyric.Line line = new Lyric.Line();
                        line.setCurrentDuration(parserTime(timeStr));
                        if (lineList.size() > 0) {
                            lineList.get(lineList.size() - 1).setNextDuration(line.getCurrentDuration());
                        }
                        line.setText(lineResult.substring(lineResult.indexOf("]") + 1));
                        lineList.add(line);
                    }
                }
            }
            if (lineList.size() > 0) {
                lineList.get(lineList.size() - 1).setNextDuration(music.getDuration());
            }
            // TODO modify add
            // filter empty text line.
            Iterator<Lyric.Line> lineIterator = lineList.iterator();
            int linePos = 0;
            while (lineIterator.hasNext()) {
                Lyric.Line nextLine = lineIterator.next();
                if (TextUtils.isEmpty(nextLine.getText())) {
                    lineIterator.remove();
                } else {
                    nextLine.setLinePosition(linePos);
                    linePos++;
                }
            }
            // resolve some lyric file base info isn't exits.
            if (TextUtils.isEmpty(lyric.getTitle())) {
                lyric.setTitle(music.getName());
            }
            if (TextUtils.isEmpty(lyric.getArtist())) {
                lyric.setArtist(music.getArtist());
            }
            if (TextUtils.isEmpty(lyric.getAlbum())) {
                lyric.setAlbum(music.getAlbumName());
            }
            // TODO modify add
            mLyric = lyric;
            if (mCallback != null) {
                mCallback.onLyricChange(mLyric);
            }
        } catch (Exception e) {
            mLyric = null;
            if (mCallback != null) {
                mCallback.onLyricChange(null);
            }
            Log.d(TAG, "setResourcePath has an error : " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int parserTime(String string) {
        int start = string.indexOf("[") + 1;
        int end = string.indexOf("]");
        string = string.substring(start, end);
        string = string.replace(".", ":");
        String[] timeData = string.split(":");
        // 分离出分、秒并转换为整型
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);
        // 计算上一行与下一行的时间转换为毫秒数
        return (minute * 60 + second) * 1000 + millisecond * 10;
    }

    public void start(int startDuration) {
        Log.d(TAG, "startDuration = " + startDuration);
        if (mLyric != null) {
            List<Lyric.Line> lineList = mLyric.getLineList();
            int size = lineList.size();
            if (size == 0) {
                return;
            }
            if (size == 1) {
                mHandler.removeCallbacks(mUpdateTask);
                mLineNumber = 0;
                Lyric.Line line = lineList.get(mLineNumber);
                if (mCallback != null) {
                    mCallback.onLineUpdate(line);
                }
            } else {
                int startNumber = -1;
                for (int i = 0; i < size; i++) {
                    Lyric.Line line = lineList.get(i);
                    int currentDuration = line.getCurrentDuration();
                    int nextDuration = line.getNextDuration();
                    if (i == 0 && startDuration < currentDuration) {
                        startNumber = i;
                        break;
                    } else if (startDuration >= currentDuration && startDuration < nextDuration) {
                        startNumber = i;
                        break;
                    }
                }
                if (startNumber != -1) {
                    mHandler.removeCallbacks(mUpdateTask);
                    mLineNumber = startNumber;
                    Lyric.Line line = lineList.get(mLineNumber);
                    if (mCallback != null) {
                        mCallback.onLineUpdate(line);
                    }
                    int nextLinerNumber = mLineNumber + 1;
                    if (nextLinerNumber >= 0 && nextLinerNumber < lineList.size()) {
                        mLineNumber = nextLinerNumber;
                        int durationOffset = lineList.get(mLineNumber).getCurrentDuration() - startDuration;
                        durationOffset = Math.max(durationOffset, 0);
                        mHandler.postDelayed(mUpdateTask, durationOffset);
                    }
                }
            }
        }
    }

    public void stop() {
        mHandler.removeCallbacks(mUpdateTask);
    }

    private void update() {
        if (mLyric != null) {
            List<Lyric.Line> lineList = mLyric.getLineList();
            if (mLineNumber >= 0 && mLineNumber < lineList.size()) {
                mHandler.removeCallbacks(mUpdateTask);
                Lyric.Line currentLine = lineList.get(mLineNumber);
                if (mCallback != null) {
                    mCallback.onLineUpdate(currentLine);
                }
                int nextLinerNumber = mLineNumber + 1;
                if (nextLinerNumber >= 0 && nextLinerNumber < lineList.size()) {
                    mLineNumber = nextLinerNumber;
                    Lyric.Line nextLine = lineList.get(mLineNumber);
                    int durationOffset = nextLine.getCurrentDuration() - currentLine.getCurrentDuration();
                    durationOffset = Math.max(durationOffset, 0);
                    mHandler.postDelayed(mUpdateTask, durationOffset);
                }
            }
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onLyricChange(@Nullable Lyric lyric);

        void onLineUpdate(@NonNull Lyric.Line line);
    }
}
