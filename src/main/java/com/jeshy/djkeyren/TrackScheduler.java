package com.jeshy.djkeyren;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final DiscordApi api;
    private final AudioConnection audioConnection;
    private final TextChannel channel;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player, DiscordApi api, AudioConnection audioConnection,TextChannel channel) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.api = api;
        this.audioConnection = audioConnection;
        this.channel = channel;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack(boolean sendMessage) {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        if(queue.isEmpty()){
            audioConnection.close();
        }else{
            if(sendMessage) {
                AudioTrack audioTrack = queue.peek();
                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor("Now Playing", "", "")
                        .setColor(Color.cyan)
                        .setTitle(audioTrack.getInfo().title)
                        .setUrl(audioTrack.getInfo().uri);
                channel.sendMessage(embed);
            }

            player.startTrack(queue.poll(), false);
        }
    }

    public void stopMusic(){
        while(!queue.isEmpty()){
            nextTrack(false);
        }
        nextTrack(false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack(true);
        }
    }
}
