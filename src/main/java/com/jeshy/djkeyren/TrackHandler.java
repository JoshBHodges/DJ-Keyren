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
    public void trackLoaded(AudioTrack audioTrack) {
        try {
            this.trackScheduler.queue(audioTrack, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Added to queue", "", user.getAvatar())
                .setColor(Color.cyan);

        playlist.getTracks().forEach(audioTrack -> {
            TrackInfo trackInfo = TrackInfo.createTrack(audioTrack.getInfo());
            embed.addInlineField(trackInfo.title, String.valueOf(trackInfo.getLength()));
            try {
                this.trackScheduler.queue(audioTrack, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
