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

        if (!api.getYourself().getConnectedVoiceChannels().isEmpty()) {
            api.getYourself().getConnectedVoiceChannels().forEach(connectedVC -> {
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
                } else {
                    if (api.getYourself().getConnectedVoiceChannels().isEmpty()) {
                        voiceChannel = event.getMessageAuthor().getConnectedVoiceChannel().get();
                        try {
                            musicPlayer = new MusicPlayer(api, voiceChannel.connect().get(),event.getChannel());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    String url;
                    boolean isLink = LinkParser.isLink(message.substring(6));

                    if(isLink) {
                        url = message.split(" ")[1];
                    }else {
                        url = LinkParser.YTSearch(message.substring(6));
                    }

                    musicPlayer.playSong(url, user);
                }
            }

            if (message.startsWith(".next")) {
                musicPlayer.getTrackScheduler().nextTrack(true);
            }

            if (message.startsWith(".stop")) {
                musicPlayer.getTrackScheduler().stopMusic();
            }


        });
    }
}