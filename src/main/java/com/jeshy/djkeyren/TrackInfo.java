package com.jeshy.djkeyren;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class TrackInfo extends AudioTrackInfo {
    /**
     * @param title      TrackInfo title
     * @param author     TrackInfo author, if known
     * @param length     Length of the track in milliseconds
     * @param identifier Audio source specific track identifier
     * @param isStream   True if this track is a stream
     * @param uri        URL of the track or path to its file.
     */
    public TrackInfo(String title, String author, long length, String identifier, boolean isStream, String uri) {
        super(title, author, length, identifier, isStream, uri);
    }

    public static TrackInfo createTrack(AudioTrackInfo info) {
      return new TrackInfo(info.title,info.author, info.length, info.identifier, info.isStream, info.uri);
    };

    public String getLength(){
        long milliseconds = this.length;

        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;

        return String.format("%d:%02d", minutes, seconds);
    }
}
