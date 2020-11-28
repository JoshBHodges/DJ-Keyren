package com.jeshy.djkeyren;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class TrackHandler implements AudioLoadResultHandler {
    private final TextChannel channel;
    private final TrackScheduler trackScheduler;
    private User user;

    public TrackHandler(TrackScheduler trackScheduler, TextChannel channel) {
        this.trackScheduler = trackScheduler;
        this.channel = channel;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Added to queue", "", user.getAvatar())
                .setColor(Color.cyan)
                .setTitle(track.getInfo().title)
                .setUrl(track.getInfo().uri);
        channel.sendMessage(embed);
        this.trackScheduler.queue(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Added to queue", "", user.getAvatar())
                .setColor(Color.cyan);
        playlist.getTracks().forEach(audioTrack -> {
            TrackInfo trackInfo = TrackInfo.createTrack(audioTrack.getInfo());
            embed.addInlineField(trackInfo.title,String.valueOf(trackInfo.getLength()));
            this.trackScheduler.queue(audioTrack);
        });
        System.out.println();
        channel.sendMessage(embed);
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
