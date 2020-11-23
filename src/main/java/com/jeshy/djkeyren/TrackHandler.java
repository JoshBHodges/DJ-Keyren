package com.jeshy.djkeyren;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class TrackHandler implements AudioLoadResultHandler {
    private final AudioPlayer player;
    private final TextChannel channel;
    private final TrackScheduler trackScheduler;
    private User user;

    public TrackHandler(AudioPlayer player, TextChannel channel, TrackScheduler trackScheduler) {
        this.player = player;
        this.channel = channel;
        this.trackScheduler = trackScheduler;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Added to queue","",user.getAvatar())
                .setColor(Color.cyan)
                .setTitle(track.getInfo().title)
                .setUrl(track.getInfo().uri);
        channel.sendMessage(embed);
        this.trackScheduler.queue(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        playlist.getTracks().forEach(audioTrack -> {
            channel.sendMessage("**Added to queue** " + audioTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")");
            this.trackScheduler.queue(audioTrack);
        });
    }

    @Override
    public void noMatches() {
        channel.sendMessage("Nothing found");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        channel.sendMessage("Could not play: " + exception.getMessage());
    }

    public void setUser(User user) {
        this.user = user;
    }
}