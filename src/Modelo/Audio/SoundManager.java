package Modelo.Audio;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class SoundManager {
    
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private Clip musicClip;
    private float volume = 0.5f; // 50% volume
    
    private SoundManager() {
        soundClips = new HashMap<>();
        loadSounds();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    private void loadSounds() {
        try {
            // Carrega todos os sons
            loadSound("button", "src/res/sounds/game-start-reset.mp3");
            loadSound("collect", "src/res/sounds/retro-coin-4-236671.mp3");
            loadSound("damage", "src/res/sounds/video-game-hit-noise-001-135821.mp3");
            loadSound("shoot", "src/res/sounds/laser1.mp3");
            loadSound("gameover", "src/res/sounds/game-over.mp3");
            loadSound("win", "src/res/sounds/winsquare-6993.mp3");
            
            // Carrega música de fundo
            loadMusic("src/res/sounds/music-game-loop.mp3");
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar sons: " + e.getMessage());
        }
    }
    
    private void loadSound(String name, String path) {
        try {
            File soundFile = new File(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            setVolume(clip, volume);
            soundClips.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao carregar som " + name + ": " + e.getMessage());
        }
    }
    
    private void loadMusic(String path) {
        try {
            File musicFile = new File(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
            setVolume(musicClip, volume);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao carregar música: " + e.getMessage());
        }
    }
    
    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    public void playSound(String soundName) {
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void playMusic() {
        if (musicClip != null) {
            musicClip.setFramePosition(0);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }
    
    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }
    
    public void setMasterVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        // Atualiza volume de todos os clips
        if (musicClip != null) {
            setVolume(musicClip, this.volume);
        }
        
        for (Clip clip : soundClips.values()) {
            setVolume(clip, this.volume);
        }
    }
    
    public void cleanup() {
        stopMusic();
        stopAllSounds();
        
        if (musicClip != null) {
            musicClip.close();
        }
        
        for (Clip clip : soundClips.values()) {
            clip.close();
        }
        
        soundClips.clear();
    }
}