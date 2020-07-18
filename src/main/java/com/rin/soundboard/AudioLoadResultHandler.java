package com.rin.soundboard;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;

public class AudioLoadResultHandler implements com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler {
    AudioPlayer player = null;
    MusicManager manager = null;
    Thread thread ;
    public AudioLoadResultHandler(AudioPlayer player, MusicManager manager) {
        this.player = player;
        this.manager = manager;
    }
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        if(player.getPlayingTrack() == null) {
            System.out.println("Sexing");
            player.playTrack(audioTrack);
        } else {
            manager.scheduler.queue(audioTrack);
        }

    }



    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.isSearchResult()) {
            if(player.getPlayingTrack() == null) {
                player.playTrack(playlist.getTracks().get(0));
            } else {
                manager.scheduler.queue(playlist.getTracks().get(0));
            }
        } else {
            playlist.getTracks().stream().forEach(AudioTrack -> manager.scheduler.queue(AudioTrack));
        }
    }

    @Override
    public void noMatches() {
        try {
            thread.join();
        } catch (InterruptedException ignored) {

        }

    }

    @Override
    public void loadFailed(FriendlyException e) {
        try {
            thread.join();
        } catch (InterruptedException ignored) {

        }
    }

    public static String formatTiming(long timing, long maximum) {
        timing = Math.min(timing, maximum) / 1000;

        long seconds = timing % 60;
        timing /= 60;
        long minutes = timing % 60;
        timing /= 60;
        long hours = timing;

        if (maximum >= 3600000L) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}
