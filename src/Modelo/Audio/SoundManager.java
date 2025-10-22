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
    private float volume = 0.8f;
    
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
            loadSound("button", "src/res/sounds/game-start-reset.wav");
            loadSound("collect", "src/res/sounds/retro-coin-4-236671.wav");
            loadSound("damage", "src/res/sounds/video-game-hit-noise-001-135821.wav");
            loadSound("shoot", "src/res/sounds/laser1.wav");
            loadSound("gameover", "src/res/sounds/game-over.wav");
            loadSound("win", "src/res/sounds/winsquare-6993.wav");
            
            loadMusic("src/res/sounds/music-game-loop.wav");
            
            System.out.println("✅ Todos os sons carregados com sucesso!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar sons: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadSound(String name, String path) {
        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                System.err.println("❌ Arquivo não encontrado: " + path);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            setVolume(clip, volume);
            soundClips.put(name, clip);
            System.out.println("✅ Som carregado: " + name);
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("❌ Formato não suportado para " + name + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Erro de IO ao carregar " + name + ": " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("❌ Linha de áudio indisponível para " + name + ": " + e.getMessage());
        }
    }
    
    private void loadMusic(String path) {
        try {
            File musicFile = new File(path);
            if (!musicFile.exists()) {
                System.err.println("❌ Música não encontrada: " + path);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
            setVolume(musicClip, volume * 0.6f);
            System.out.println("✅ Música carregada!");
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("❌ Formato de música não suportado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Erro de IO ao carregar música: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("❌ Linha de áudio indisponível para música: " + e.getMessage());
        }
    }
    
    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            dB = Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum()));
            gainControl.setValue(dB);
        }
    }
    
    public void playSound(String soundName) {
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
            System.out.println("🔊 Tocando som: " + soundName);
        } else {
            System.err.println("❌ Som não encontrado: " + soundName);
        }
    }
    
    public void playMusic() {
        if (musicClip != null) {
            if (musicClip.isRunning()) {
                musicClip.stop();
            }
            musicClip.setFramePosition(0);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            System.out.println("🎵 Música iniciada!");
        } else {
            System.err.println("❌ Música não carregada!");
        }
    }
    
    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            System.out.println("⏹️ Música parada!");
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
        
        if (musicClip != null) {
            setVolume(musicClip, this.volume * 0.6f);
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
        System.out.println("🧹 Recursos de áudio limpos!");
    }
}