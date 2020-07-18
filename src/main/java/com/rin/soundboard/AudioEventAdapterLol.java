package com.rin.soundboard;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;

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

    public void onEvent(AudioEvent audioEvent) {

        if(audioEvent instanceof TrackStartEvent) {
            Image thumbnail = new Image("https://img.youtube.com/vi/"+((TrackStartEvent) audioEvent).track.getIdentifier()+"/mqdefault.jpg");
            Platform.runLater(() ->  {
                YTTHUMBNAIL.setImage(thumbnail);
                PLAYINGRN.setText(((TrackStartEvent) audioEvent).track.getInfo().title);
                PLAYINGRN.setOnAction(event -> bruheee.showDocument(((TrackStartEvent) audioEvent).track.getInfo().uri));

            });
            task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String position;
                    String duration;
                    String positionDuration = "0:00/0:00";
                    double progress;
                    while(true) {
                        if(!player.isPaused()) {
                            duration = AudioLoadResultHandler.formatTiming(player.getPlayingTrack().getDuration(), player.getPlayingTrack().getDuration());
                            position = AudioLoadResultHandler.formatTiming(player.getPlayingTrack().getPosition(), player.getPlayingTrack().getDuration());
                            progress = (1f / player.getPlayingTrack().getDuration() * player.getPlayingTrack().getPosition());
                            if (!positionDuration.equalsIgnoreCase(position + "/" + duration)) {
                                positionDuration = position + "/" + duration;
                                String finalPositionDuration = positionDuration;

                                double finalProgress = progress;
                                if(YTTHUMBNAIL.getImage() == thumbnail) {
                                    Platform.runLater(() -> {
                                        DURATION.setText(finalPositionDuration);
                                        BAR.setProgress(finalProgress);
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        DURATION.setText(finalPositionDuration);
                                        BAR.setProgress(finalProgress);
                                        YTTHUMBNAIL.setImage(thumbnail);
                                    });
                                }

                            }
                        }
                    }
                }
            };
            new Thread(task).start();

        } else if(audioEvent instanceof TrackEndEvent) {
            Platform.runLater(() -> {
                    if(manager.scheduler.getQueue().isEmpty())
                    DURATION.setText("0:00/0:00");
                    BAR.setProgress(0.0);
                    YTTHUMBNAIL.setImage(new Image(getClass().getResourceAsStream("Placeholder.png")));
                    PLAYINGRN.setText("");
                    PLAYINGRN.setOnAction(null);
            });
        }
    }
}
