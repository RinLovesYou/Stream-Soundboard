package com.rin.soundboard;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Bruh.fxml"));
        primaryStage.setTitle("Soundboard");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.getIcons().add(new Image("file:"+getClass().getResource("resources/logo.png").toString()));
        primaryStage.show();
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, PrimaryController::closeWindowEvent);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        root.getStylesheets().add(getClass().getResource("style.css").toString());


    }



    public static void main(String[] args) {
        launch(args);
    }

    public static List<String> getMixers() {
        final List<String> mixers = new ArrayList<>();

        // Obtains an array of mixer info objects that represents the set of
        // audio mixers that are currently installed on the system.
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
    public static Mixer getMixer(final String name) {
        Mixer mixer = null;

        // Obtains an array of mixer info objects that represents the set of
        // audio mixers that are currently installed on the system.
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
