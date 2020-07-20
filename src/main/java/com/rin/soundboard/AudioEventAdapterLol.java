package com.rin.soundboard;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.rin.soundboard.BruhController.*;

public class AudioEventAdapterLol extends AudioEventAdapter {
    private AudioPlayer player;
    public static Task<Void> task;
    public static HostServicesDelegate bruheee;
    MusicManager manager;
    public AudioEventAdapterLol(AudioPlayer player, MusicManager manager) {
        this.player = player;
        this.manager = manager;
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

    private Runnable progressbare;
    private ScheduledExecutorService executor = null;

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        Image thumbnail = new Image("https://img.youtube.com/vi/"+track.getIdentifier()+"/mqdefault.jpg");
        Platform.runLater(() ->  {
            YTTHUMBNAIL.setImage(thumbnail);
            PLAYINGRN.setText(track.getInfo().title);
            PLAYINGRN.setOnAction(event -> bruheee.showDocument(track.getInfo().uri));

        });
        AtomicReference<String> position = new AtomicReference<>("");
        AtomicReference<String> positionDuration = new AtomicReference<>("0:00/0:00");
        AtomicReference<Double> progress = new AtomicReference<>((double) 0);
        AtomicReference<String> duration = new AtomicReference<>("");

        progressbare = () -> {
            duration.set(formatTiming(player.getPlayingTrack().getDuration(), player.getPlayingTrack().getDuration()));
            position.set(formatTiming(player.getPlayingTrack().getPosition(), player.getPlayingTrack().getDuration()));
            progress.set((double) (1f / player.getPlayingTrack().getDuration() * player.getPlayingTrack().getPosition()));
            positionDuration.set(position + "/" + duration);
            Platform.runLater(() -> {
                DURATION.setText(positionDuration.get());
                BAR.setProgress(progress.get());
            });
        };

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(progressbare, 0, 1, TimeUnit.SECONDS);



    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        executor.shutdown();
        Platform.runLater(() -> {
            DURATION.setText("0:00/0:00");
            BAR.setProgress(0.0);
            PLAYINGRN.setText("");
            PLAYINGRN.setOnAction(null);
        });

    }
}
