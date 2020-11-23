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

    public void playSong(String song, User user, TextChannel textChannel){
        trackHandler.setUser(user);
        this.trackHandler.setChannel(textChannel);
        playerManager.loadItem(song,trackHandler);
    }

    public MusicPlayer(DiscordApi api, AudioConnection audioConnection, String song, TextChannel textChannel, User user) {
        MusicPlayer.api = api;

        this.playerManager = new DefaultAudioPlayerManager();
        this.playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        player = playerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(player,api);
        player.addListener(trackScheduler);

        AudioSource source = new LavaPlayerAudioSource(MusicPlayer.api, player);
        audioConnection.setAudioSource(source);

        this.trackHandler = new TrackHandler(trackScheduler);
        playSong(song,user,textChannel);

    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }
}
