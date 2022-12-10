package GoSound;

import java.io.IOException;
import javax.sound.sampled.*;

public class AudioPlay {
    Clip                clip;
    AudioInputStream    audioStream;
    private float       volume = -20.0f;

    public float    getVolume()             { return volume;        }
    public void     setVolume(float volume) { this.volume = volume; }

    public boolean isPlaying() {
        return (clip != null) && clip.isRunning();
    }

    public AudioPlay(AudioInputStream stream) {
        loadMusic(stream);
    }

    public void playMusic() {
        if (clip.isRunning()) {
            clip.stop();
            clip.flush();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    private void loadMusic(AudioInputStream stream) {
        audioStream = stream;

        try {
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (LineUnavailableException | IOException e) {
            System.out.println("[ERROR] Cannot create clip");
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        clip.stop();
        clip.flush();
    }

    public void recycle() {
        try {
            clip.close();
        } finally {
            clip = null;
        }
    }
}