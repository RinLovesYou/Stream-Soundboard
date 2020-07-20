package com.rin.soundboard;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.sound.sampled.*;
import javax.swing.*;

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
    private Label queueitems;
    @FXML
    private Slider volumeSlider;


    public static Label QITEMS;

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
    private ComboBox<String> mixerBox;

    @FXML
    private Button btnPause;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnSkip;

    @FXML
    private TextField quickplay;

    public void initialize(){
        ObservableList<String> list = FXCollections.observableArrayList(getMixers());
        mixerBox.setValue("VoiceMeeter Input (VB-Audio VoiceMeeter VAIO)");
        mixerBox.setItems(list);
        YTTHUMBNAIL = thumbnail;
        DURATION = durationNum;
        BAR = progressbar;
        PLAYINGRN = playing;
        QITEMS = queueitems;
        player.addListener(new AudioEventAdapterLol(player, musicManager));

    }

    @FXML
    void dragEvent(MouseEvent event) {
        player.setVolume((int)volumeSlider.getValue());
    }

    @FXML
    void clickEvent(MouseEvent event) {
        player.setVolume((int)volumeSlider.getValue());
    }

    @FXML
    void qpkeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            if (yeah == null) {
                    yeah = new Thread() {
                        @Override
                        public void run() {
                            try {
                                if(mixerBox.getValue() != null)  {
                                    play(quickplay.getText(), getMixer(mixerBox.getValue()), this);
                                }
                            } catch (LineUnavailableException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    yeah.start();
            }
            else if (quickplay.getText().startsWith("https://") || quickplay.getText().startsWith("http://")) {
                System.out.println("loading in mainthread");
                manager.loadItem(quickplay.getText(), handler);

            } else {
                System.out.println("loading surch in mainthread");
                manager.loadItem("ytsearch: " + quickplay.getText(), handler);
            }
        }
    }

    @FXML
    void handleClicks(ActionEvent event) {
        if(event.getSource().equals(btnSkip)) {
            if(!musicManager.scheduler.getQueue().isEmpty()) {
                player.stopTrack();
                musicManager.scheduler.nextTrack();
            }
        } else if(event.getSource().equals(btnStop)) {
            if(player.getPlayingTrack() != null) {
                player.stopTrack();
                player.destroy();
            }
        } else if(event.getSource().equals(btnPause)) {
            if(yeah.isAlive()) {
                System.out.println("i'm trying dude");
                player.setPaused(!player.isPaused());
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

        //btnPause
        //btnStop
        //btnSkip


        AudioDataFormat format = manager.getConfiguration().getOutputFormat();
        AudioInputStream stream = AudioPlayerInputStream.createStream(player, format, 10000L, true);
        SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, stream.getFormat());
        SourceDataLine line = (SourceDataLine) mixer.getLine(info);

        line.open(stream.getFormat());
        line.start();


        byte[] buffer = new byte[COMMON_PCM_S16_BE.maximumChunkSize()];

        SwingWorker<Object, Object> woker = new SwingWorker<Object, Object>() {
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

