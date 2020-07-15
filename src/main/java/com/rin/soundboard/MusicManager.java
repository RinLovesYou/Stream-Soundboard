package com.rin.soundboard;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;


public class MusicManager {

    /**
     * Audio player for the guild.
     */
    public AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final MusicPlayer scheduler;

    /**
     * Creates a player and a track scheduler.
     * @param manager Audio player manager to use for creating the player.
     */
    public MusicManager(AudioPlayerManager manager, AudioPlayer audioPlayer, Thread thread) {
        player = manager.createPlayer();
        this.player = audioPlayer;
        scheduler = new MusicPlayer(player, thread);
        this.player.addListener(scheduler);
    }

}

