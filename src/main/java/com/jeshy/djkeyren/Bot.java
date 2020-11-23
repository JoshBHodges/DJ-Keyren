package com.jeshy.djkeyren;


import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.user.User;

import java.util.Optional;

public class Bot {

    private static ServerVoiceChannel voiceChannel;
    private static MusicPlayer musicPlayer;

    public static void main(String[] args) {

        //Login the bot
        DiscordApi api = new DiscordApiBuilder()
                .setToken(System.getenv("BOT_TOKEN"))
                .login().join();

        if(!api.getYourself().getConnectedVoiceChannels().isEmpty()){
            api.getYourself().getConnectedVoiceChannels().forEach(connectedVC ->{
                try {
                    connectedVC.connect().get().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        api.addMessageCreateListener(event -> {
            String message = event.getMessage().getContent();
            User user = event.getMessageAuthor().asUser().get();

            if (message.startsWith(".play")) {
                if (event.getMessageAuthor().getConnectedVoiceChannel().equals(Optional.empty())) {
                    new MessageBuilder()
                            .append("<@" + event.getMessageAuthor().getId() + "> **You need to be in a voice channel!**")
                            .send(event.getChannel());
                }else {

                    String url;
                    if (!YTS.isLink(message.split(" ")[1])) {
                        System.out.println(message.substring(6));
                        url = YTS.YTSearch(message.substring(6));
                    } else {
                        url = message.split(" ")[1];
                    }

                    if (!api.getYourself().getConnectedVoiceChannels().isEmpty()) {
                        musicPlayer.playSong(url, user);
                    } else {
                        voiceChannel = event.getMessageAuthor().getConnectedVoiceChannel().get();
                        voiceChannel.connect().thenAccept(audioConnection -> {
                            musicPlayer = new MusicPlayer(api, audioConnection, url, event.getChannel(), user);
                        }).exceptionally(e -> {
                            // Failed to connect to voice channel (no permissions?)
                            e.printStackTrace();
                            return null;
                        });
                    }
                }
            }

            if (message.startsWith(".next")){
                musicPlayer.getTrackScheduler().nextTrack();
            }


        });
    }
}