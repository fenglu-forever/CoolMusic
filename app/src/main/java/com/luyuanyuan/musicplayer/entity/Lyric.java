package com.luyuanyuan.musicplayer.entity;

import java.util.ArrayList;
import java.util.List;

public class Lyric {
    private String title;
    private String artist;
    private String album;
    private final List<Line> lineList = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", lineList=" + lineList +
                '}';
    }

    public static class Line {
        private String text;
        private int currentDuration;
        private int nextDuration;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getCurrentDuration() {
            return currentDuration;
        }

        public void setCurrentDuration(int currentDuration) {
            this.currentDuration = currentDuration;
        }

        public int getNextDuration() {
            return nextDuration;
        }

        public void setNextDuration(int nextDuration) {
            this.nextDuration = nextDuration;
        }
    }
}
