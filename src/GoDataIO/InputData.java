package GoDataIO;

import javafx.scene.image.Image;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class InputThread extends IOThread {
    List<String[]>  fileList;

    List<File>      directoryList;
    List<Integer>   dirIndexList;

    public InputThread(String name) {
        super(name);

        directoryList = new ArrayList<>();
        dirIndexList  = new ArrayList<>();
    }

    public Object[] inputFromDirectory(File[] files) {
        Object[] result = new Object[files.length + 1];
        int count = 0;
        for (File file : files) {
            try {
                if (file.isDirectory()) continue;
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder  sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                result[++count] = sb.toString();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void inputFromDirectoryWriteToData(File fi, int index) {
        Object[] data = inputFromDirectory(Objects.requireNonNull(fi.listFiles()));
        int len = data.length;
        ioStream[index] = len;
        if (len - 1 >= 0) System.arraycopy(data, 1, ioStream, index + 1, len - 1);
    }

    @Override
    public void ioAction() {
        for (String[] file : fileList) {
            try {
                int id = Integer.parseInt(file[0]);
                String name = file[1];
                File fi = new File(name);

                if (fi.isDirectory()) {
                    dirIndexList .add(id);
                    directoryList.add(fi);

                    inputFromDirectoryWriteToData(fi, id);
                    continue;
                }

                switch (id / 100) {
                    case 1 -> ioStream[id] = new FileInputStream(name);
                    case 2 -> ioStream[id] = AudioSystem.getAudioInputStream(fi);
                }
            } catch (IOException e) {
                System.out.println("[ERROR]" + file[1] + " not found.");
            } catch (UnsupportedAudioFileException e) {
                System.out.println("[ERROR]" + file[1] + " unsupported audio file.");
            }
        }
    }

    @Override
    public void additionAction() {
        int len = directoryList.size();
        for (int i = 0; i < len; i++)
            inputFromDirectoryWriteToData(directoryList.get(i), dirIndexList.get(i));
//        System.out.println("[DEBUG] Finished Input File from directory");
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
    InputThread input;
    public Object[] data = new Object[1000];

    public Object[] getData()           { return data;                              }
    public int      getSavesCount()     { return (int)data[300];                    }
    public String   getGoGame(int id)   { return (String) data[id];                 }
    public Image    getImage(int id)    { return new Image((InputStream) data[id]); }

    public void refreshReadSave() {
        input.reStartRead();
    }

    public InputData() {
        BufferedReader files;
        try {
            files = new BufferedReader(new FileReader("data/dataPath.txt"));
        } catch (FileNotFoundException e) {
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

    public void release() {
        input.clean();
    }
}
