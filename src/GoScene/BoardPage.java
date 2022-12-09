package GoScene;

import GoDataIO.InputData;
import GoGame.GoMain;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.Objects;

public class BoardPage extends ButtonPages{
    ChessBoard board;
    BorderPane rootPane;
    InputData data;

    int savesNumber = 0;

    /**
     * serialize: serialize the object to local storage
     * @param obj: the object to be serialized
     * @param index: the file number
     */
    static void serialize(Object obj, int index) throws IOException {
        ObjectOutputStream objectOutputStream =
                new ObjectOutputStream(new FileOutputStream("data\\saves\\save" + index + ".dat"));
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
    }

    /**
     * deserialize: read the object from file
     * @param pathName: the path of the file
     */
    public Object deserialize(String pathName) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream =
                new ObjectInputStream(new FileInputStream(pathName));
        Object obj = objectInputStream.readObject();
        objectInputStream.close();

        return obj;
    }

    /**
     * set all the object on the page
     */
    public BoardPage(InputData inputData) throws FileNotFoundException {
        initialButton(new String[]{"认输", "虚着", "读档", "存档", "退出"}, 170, 80);
        data = inputData;

        board       = new ChessBoard(data);
        rootPane    = new BorderPane();
        scene       = new Scene(rootPane);

        VBox buttonLayout = new VBox();

        /* set the layout of the button */
        buttonLayout.setLayoutX(1100);
        buttonLayout.setLayoutY(350 );
        buttonLayout.setSpacing(20  );

        /* put the button one the pane */
        for (int i = 0; i < buttonNumber; i++)
            buttonLayout.getChildren().add(button[i].getButton());

        /* put the button and pane on the rootPane */
        rootPane.setRight(buttonLayout);
        rootPane.setLeft(board.getPane());

        setButtonAction(1, () -> board.skipTurn());

        /* set the "读档" action */
        // TODO:
        //  - choose the saves
        setButtonAction(2, () -> {
            if (inputData.getSavesCount() == 0) {
                System.out.println("[ERROR] No saves found.");
                return;
            }

            inputData.refreshReadSave();
            board.recoverPieces(inputData.getGoGame(301));
        });

        /* set the "存档" action */
        setButtonAction(3, () -> {
            int curNum = inputData.getSavesCount();
            String data = board.toString();
            File file = new File("data/saves/" + curNum + ".sgf");
            FileOutputStream fop = null;
            try {
                fop = new FileOutputStream(file);
                if (!file.exists())
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                byte[] contentInBytes = data.getBytes();
                fop.write(contentInBytes);
            } catch (IOException e) {
                System.out.println("[ERROR] Cannot save the game into " + curNum + ".sgf");
            }
        });
    }

    /**
     * clear: clear the board
     */
    @Override
    public void clear() {
        board.clear();
        board.refreshSound();
    }

    /**
     * cleanPages: clear the pages when ended
     */
    @Override
    public void cleanPages() {
        board.clean();
    }
}
