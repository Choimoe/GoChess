package GoSound;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlay {
    AudioInputStream    audioStream;
    AudioFormat         audioFormat;
    DataLine.Info       dataLineInfo;
    SourceDataLine      sourceDataLine;

    int     length;
    byte[]  buf = new byte[1024];

    public AudioPlay(String path) {
        loadMusic(path);
    }

    public void playMusic() {
        try {
            audioStream.mark(length);

            int count;
            while ((count = audioStream.read(buf, 0, buf.length)) != -1) {
                sourceDataLine.write(buf, 0, count);
            }

            audioStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMusic(String path) {
        try {
            audioStream     = AudioSystem.getAudioInputStream(new File(path));
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        /* get the encode method */
        audioFormat         = audioStream.getFormat();
        dataLineInfo        = new DataLine.Info(SourceDataLine.class,
                audioFormat, AudioSystem.NOT_SPECIFIED);

        try {
            sourceDataLine  = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        sourceDataLine.start();

        try {
            length = audioStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recycle() {
        sourceDataLine.drain();
        sourceDataLine.close();

        try {
            audioStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String path = "assets\\putPiece.wav";
        new AudioPlay(path);
    }
}