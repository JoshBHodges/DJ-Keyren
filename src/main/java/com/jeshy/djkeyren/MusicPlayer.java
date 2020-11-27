package com.jeshy.djkeyren;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;

public class MusicPlayer{
    public static DiscordApi api;
    private final AudioPlayerManager playerManager;
    private final TrackHandler trackHandler;
    private final TrackScheduler trackScheduler;
    private final AudioPlayer player;

    public void playSong(String song, User user){
        trackHandler.setUser(user);
        playerManager.loadItem(song,trackHandler);
    }

    public MusicPlayer(DiscordApi api, AudioConnection audioConnection,TextChannel channel) {
        MusicPlayer.api = api;
        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(player,api,audioConnection,channel);
        trackHandler = new TrackHandler(trackScheduler,channel);
        player.addListener(trackScheduler);
        AudioSource source = new LavaPlayerAudioSource(MusicPlayer.api, player);
        audioConnection.setAudioSource(source);
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }
}
