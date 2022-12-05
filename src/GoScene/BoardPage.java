package GoScene;

import GoGame.GoMain;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.Objects;

public class BoardPage extends ButtonPages{
    ChessBoard board;
    BorderPane rootPane;

    int savesNumber = 0;

    /**
     * serialize: serialize the object to local storage
     * @param obj: the object to be serialized
     * @param index: the file number
     */
    static void serialize(Object obj, int index) throws IOException {
        ObjectOutputStream objectOutputStream =
                new ObjectOutputStream(new FileOutputStream("data\\save" + index + ".dat"));
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
    public BoardPage() throws FileNotFoundException {
        initialButton(new String[]{"认输", "虚着", "读档", "存档", "退出"}, 170, 80);

        board = new ChessBoard();

        rootPane = new BorderPane();
        scene = new Scene(rootPane);

        VBox buttonLayout = new VBox();

        /* set the layout of the button */
        buttonLayout.setLayoutX(1100);
        buttonLayout.setLayoutY(350);
        buttonLayout.setSpacing(20);

        /* put the button one the pane */
        for (int i = 0; i < buttonNumber; i++)
            buttonLayout.getChildren().add(button[i].getButton());

        /* put the button and pane on the rootPane */
        rootPane.setRight(buttonLayout);
        rootPane.setLeft(board.getPane());

        setButtonAction(1, () -> board.skipTurn());

        /* set the "读档" action */
        setButtonAction(2, () -> {
            Object obj = null;
            try {
                /* search all the files on the ./data/ */
                File filePoint = new File("data");
                File[] list = filePoint.listFiles();
                for (File file : Objects.requireNonNull(list)) {
                    String str = file.getName();
                    if (!str.endsWith(".dat")) continue;
                    if (!str.startsWith("save")) continue;
                    obj = deserialize("data\\" + str);
                    if (obj != null) break;
                }

                /* got the obj */
                if (obj != null) {
                    clear();
                    board.goGame = (GoMain) obj;
                    board.recoverPieces();
                }
            } catch (IOException | ClassNotFoundException ignored) {}
        });

        /* set the "存档" action */
        setButtonAction(3, () -> {
            try { serialize(board.goGame, ++savesNumber); } catch (IOException ignored) {}
        });
    }

    /**
     * clear: clear the board
     */
    public void clear() {
        board.clear();
    }
}