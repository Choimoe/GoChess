package GoDataIO;

import javafx.scene.image.Image;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class InputThread extends IOThread {
    List<String[]> fileList;

    public InputThread(String name) {
        super(name);
    }

    @Override
    public void ioAction() {
        for (String[] file : fileList) {
            try {
                String name = file[1];
                FileInputStream input = new FileInputStream(name);
                int id = Integer.parseInt(file[0]);
                if ((id / 100) == 2) ioStream[id] = AudioSystem.getAudioInputStream(new File(name));
                else ioStream[id] = input;
            } catch (IOException e) {
                System.out.println("[ERROR]" + file[1] + " not found.");
            } catch (UnsupportedAudioFileException e) {
                System.out.println("[ERROR]" + file[1] + " unsupported audio file.");
            }
        }
    }

    public void setFileList(List<String[]> fileList) {
        this.fileList = fileList;
    }

    public void beginThread(Object[] data) {
        synchronized(this) {
            ioStream = data;
            start();
       }
    }
}

public class InputData {
    public Object[] data = new Object[1000];

    public Image getImage(int id) {
        return new Image((InputStream) data[id]);
    }

    public Object[] getData() {
        return data;
    }

    public InputData() {
        InputThread input;

        BufferedReader files;
        try {
            files = new BufferedReader(new FileReader("data/dataPath.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] dataPath.txt not found.");
            throw new RuntimeException(e);
        }

        String line = "";
        List<String[]> fileList = new ArrayList<>();

        while (true) {
            try {
                if ((line = files.readLine()) == null) break;
            } catch (IOException e) {
                System.out.println("[ERROR] " + line + " read error.");
                throw new RuntimeException(e);
            }
            String[] elements = line.split(":");
            fileList.add(elements);
        }

        input = new InputThread("Input Thread");
        input.setFileList(fileList);
        input.beginThread(data);
    }
}
