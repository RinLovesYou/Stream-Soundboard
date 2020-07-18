package com.rin.soundboard;

import java.io.IOException;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.*;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;


import javax.swing.*;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_BE;

public class PrimaryController {
    static Thread bruh;
    static AudioPlayerManager manager = new DefaultAudioPlayerManager();
    static AudioPlayer player = manager.createPlayer();
    MusicManager musicManager = new MusicManager(manager, player, bruh);
    com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler handler = new com.rin.soundboard.AudioLoadResultHandler(player, musicManager);




    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TextField textbox;

    @FXML
    private Slider slider;

    @FXML
    private Font x11;

    @FXML
    private Color x21;

    private AudioTrack lastTrack = null;

    @FXML
    void mouseClicked(MouseEvent event) {
        double pauseStart= 0.0;
        int currentSongStartTimeInSeconds = 0;
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            if(!musicManager.scheduler.getQueue().isEmpty()) {
                player.stopTrack();
                musicManager.scheduler.nextTrack();
            }

        }
    }

    @FXML
    void keyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER))
        {
            System.out.println("run");
            if(bruh == null) {
                bruh = new Thread() {
                    @Override
                    public void run() {
                        try {
                            demo(textbox.getText(), getMixer(comboBox.getValue()), this);
                        } catch (LineUnavailableException e) {
                            e.printStackTrace();
                        }
                    }
                };
                bruh.start();
            }
            else if (textbox.getText().startsWith("https://")) {
                System.out.println("loading in mainthread");
                manager.loadItem(textbox.getText(), handler);

            } else {
                System.out.println("loading surch in mainthread");
                manager.loadItem("ytsearch: " + textbox.getText(), handler);
            }

        }
    }

    public static void closeWindowEvent(WindowEvent event) {
        System.out.println("Window close request ...");
        if (player.getPlayingTrack() != null) {
            player.stopTrack();
            player.destroy();

        }
        if(bruh != null) {
            bruh.stop();
        }

    }
    @FXML
    void dragDetected(MouseEvent event) {
        player.setVolume((int)slider.getValue());
        System.out.println(slider.getValue());
    }

    @FXML
    public void initialize(){
        ObservableList<String> list = FXCollections.observableArrayList(getMixers());
        comboBox.setItems(list);

    }

    @FXML
    void onAction(ActionEvent event) {

    }

    @FXML
    void shown(ActionEvent event) {

    }

    public static List<String> getMixers() {
        final List<String> mixers = new ArrayList<>();

        // Obtains an array of mixer info objects that represents the set of
        // audio mixers that are currently installed on the system.
        // curtesy of https://github.com/goxr3plus/java-stream-player
        final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

        Arrays.stream(mixerInfos).forEach(mInfo -> {
            // line info
            final Line.Info lineInfo = new Line.Info(SourceDataLine.class);
            final Mixer mixer = AudioSystem.getMixer(mInfo);

            // if line supported
            if (mixer.isLineSupported(lineInfo))
                mixers.add(mInfo.getName());

        });

        return mixers;
    }

    public Mixer getMixer(final String name) {
        Mixer mixer = null;

        // Obtains an array of mixer info objects that represents the set of
        // audio mixers that are currently installed on the system.
        // curtesy of https://github.com/goxr3plus/java-stream-player
        final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

        if (name != null)
            for (Mixer.Info mixerInfo : mixerInfos)
                if (mixerInfo.getName().equals(name)) {
                    mixer = AudioSystem.getMixer(mixerInfo);
                    break;
                }
        return mixer;
    }
    int chunkSize;

    public void demo(String input, Mixer mixer, Thread thread) throws LineUnavailableException {


        AudioSourceManagers.registerRemoteSources(manager);
        AudioSourceManagers.registerLocalSource(manager);
        manager.getConfiguration().setOutputFormat(COMMON_PCM_S16_BE);



        if (input.startsWith("https://")) {
            System.out.println("loading");
            manager.loadItem(input, handler);

        } else {
            System.out.println("loading surch");
            manager.loadItem( "ytsearch: " + input, handler);
        }


        AudioDataFormat format = manager.getConfiguration().getOutputFormat();
        AudioInputStream stream = AudioPlayerInputStream.createStream(player, format, 10000L, true);
        SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, stream.getFormat());
        SourceDataLine line = (SourceDataLine) mixer.getLine(info);

        line.open(stream.getFormat());
        line.start();


        byte[] buffer = new byte[COMMON_PCM_S16_BE.maximumChunkSize()];

        SwingWorker woker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                while ((chunkSize = stream.read(buffer)) >= 0) {
                    line.write(buffer, 0, chunkSize);
                }
                return chunkSize;
            }
        };
        woker.execute();

    }

}
