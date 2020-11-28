package com.jeshy.djkeyren;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final AudioConnection audioConnection;
    private final TextChannel channel;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player, AudioConnection audioConnection, TextChannel channel) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.audioConnection = audioConnection;
        this.channel = channel;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param audioTrack The track to play or add to queue.
     */
    public void queue(AudioTrack audioTrack,boolean sendMessage) throws ExecutionException, InterruptedException {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.

        TrackInfo trackInfo = TrackInfo.createTrack(audioTrack.getInfo());

        if (!player.startTrack(audioTrack, true)) {
            queue.offer(audioTrack);
            if(sendMessage) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor("Added to queue")
                        .setColor(Color.cyan)
                        .setTitle(trackInfo.title)
                        .setUrl(trackInfo.uri)
                        .setDescription(trackInfo.getLength());
                channel.sendMessage(embed);
            }
        } else {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("Now Playing", "", "")
                    .setColor(Color.cyan)
                    .setTitle(trackInfo.title)
                    .setUrl(trackInfo.uri)
                    .setDescription(trackInfo.getLength());
            channel.sendMessage(embed).get().addReaction("\uD83D\uDE03");
        }
    }

    public void getQueue() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor("Song Queue");
        queue.forEach(audioTrack -> {
            TrackInfo trackInfo = TrackInfo.createTrack(audioTrack.getInfo());
            embedBuilder.addField(trackInfo.title, trackInfo.getLength());
        });
        channel.sendMessage(embedBuilder);
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack(boolean sendMessage) throws ExecutionException, InterruptedException {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        if (queue.isEmpty()) {
            audioConnection.close();
        } else {
            if (sendMessage) {
                AudioTrack audioTrack = queue.peek();
                TrackInfo trackInfo = TrackInfo.createTrack(audioTrack.getInfo());

                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor("Now Playing", "", "")
                        .setColor(Color.cyan)
                        .setTitle(trackInfo.title)
                        .setUrl(trackInfo.uri)
                        .setDescription(trackInfo.getLength());
                channel.sendMessage(embed).get().addReaction("\uD83D\uDE03");
            }

            player.startTrack(queue.poll(), false);
        }
    }

    public void stopMusic() throws ExecutionException, InterruptedException {
        while (!queue.isEmpty()) {
            nextTrack(false);
        }
        nextTrack(false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack audioTrack, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            try {
                nextTrack(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
