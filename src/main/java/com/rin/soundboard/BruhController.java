package com.rin.soundboard;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;
import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.util.SimpleAudioClient;

import javax.sound.sampled.*;
import javax.swing.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_BE;

public class BruhController {


    @FXML
    public Button btnOrders;
    @FXML
    private Label durationNum;
    public static Label DURATION;

    @FXML
    public Button btnCustomers;

    @FXML
    public Button btnMenus;

    @FXML
    public Button btnPackages;

    @FXML
    public Button btnSettings;

    @FXML
    public Button btnSignout;

    @FXML
    public Pane pnlCustomer;

    @FXML
    public Pane pnlOrders;

    @FXML
    public Pane pnlMenus;

    @FXML
    public Pane pnlOverview;

    @FXML
    public VBox pnItems;
    @FXML
    public ImageView imageview;
    @FXML
    private ImageView thumbnail;

    public static ImageView YTTHUMBNAIL;

    @FXML
    private ProgressBar progressbar;
    public static ProgressBar BAR;
    @FXML
    private Hyperlink playing;
    public static Hyperlink PLAYINGRN;



    @FXML
    public void initialize(){
        System.out.println(thumbnail);
        YTTHUMBNAIL = thumbnail;
        DURATION = durationNum;
        BAR = progressbar;
        PLAYINGRN = playing;
        player.addListener(new AudioEventAdapter(player));

    }

    @FXML
    void handleClicks(ActionEvent event) {
        if(yeah == null) {
            if (event.getSource().equals(btnOrders)) {
                yeah = new Thread() {
                    @Override
                    public void run() {
                        try {
                            play("https://www.youtube.com/watch?v=fpG3BPNQepY", getMixer(getMixers().get(0)), this);
                        } catch (LineUnavailableException e) {
                            e.printStackTrace();
                        }
                    }
                };
                yeah.start();

            }
        }
    }

    static Thread yeah;
    static AudioPlayerManager manager = new DefaultAudioPlayerManager();
    static AudioPlayer player = manager.createPlayer();
    MusicManager musicManager = new MusicManager(manager, player, yeah);
    com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler handler = new com.rin.soundboard.AudioLoadResultHandler(player, musicManager);
    int chunkSize;

    public void play(String input, Mixer mixer, Thread thread) throws LineUnavailableException {


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

}

