package GoSound;

import javax.sound.sampled.AudioInputStream;
import java.util.ArrayList;
import java.util.List;

public class SoundList {
    List<AudioPlay> audio = new ArrayList<>();
    int count;

    public SoundList(Object[] data) {
        audio.add(null);

        for (int i = 201; i < 300; i++) {
            if (data[i] == null) { count = i; break; }
            audio.add(new AudioPlay((AudioInputStream) data[i]));
        }
    }

    public void play(int index) {
        if (index < 1 || index >= count) return;
        audio.get(index - 200).playMusic();
    }

    public void recycle() {
        for (AudioPlay a : audio) if (a != null) a.recycle();
    }

    public void resetAll() {
        for (AudioPlay a : audio) if (a != null) a.reset();
    }
}
