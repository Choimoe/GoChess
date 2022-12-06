package GoSound;

import java.util.ArrayList;
import java.util.List;

public class SoundList {
    List<AudioPlay> audio = new ArrayList<>();

    String[] audioPath = {"assets\\putPiece.wav"};
    int count;

    public SoundList() {
        for (String path : audioPath) audio.add(new AudioPlay(path));
        count = audioPath.length;
    }

    public void play(int index) {
        if (index < 0 || index >= count) return;
        audio.get(index).playMusic();
    }

    public void recycle() {
        for (AudioPlay a : audio) a.recycle();
    }
}
