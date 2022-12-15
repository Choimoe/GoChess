package GoScene;

import GoBoard.ChessBoard;
import GoDataIO.InputData;
import GoServer.GoClient;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.*;

public class BoardPage extends ButtonPages{
    ChessBoard board;
    BorderPane rootPane;
    InputData data;

    GoClient client;
    Thread clientThread, boardThread;

    /**
     * set all the object on the page
     */
    public BoardPage(InputData inputData, GoClient client, Thread clientThread) {
        initialButton(new String[]{"认输", "虚着", "读档", "存档", "退出"}, 170, 80);

        this.data           = inputData;
        this.client         = client;
        this.clientThread   = clientThread;

        board               = new ChessBoard(data, client);
        rootPane            = new BorderPane();
        scene               = new Scene(rootPane);
        boardThread         = new Thread(board);

        VBox buttonLayout   = new VBox();

        boardThread.start();

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

        setButtonAction(1, () -> board.requestSkipTurn());

        /* set the "读档" action */
        // TODO:
        //  - choose the saves
        setButtonAction(2, () -> {
            if (inputData.getSavesCount() == 0) {
                System.out.println("[ERROR] No saves found.");
                return;
            }

            inputData.refreshReadSave();
            String gameData = inputData.getGoGame(301);
            board.addRequest(client.request("loadSave", gameData));
        });

        /* set the "存档" action */
        setButtonAction(3, () -> saveGo(inputData));
    }

    private void saveGo(InputData inputData) {
        int curNum = inputData.getSavesCount();
        String data = board.toString();
        File file = new File("data/saves/" + curNum + ".sgf");
        FileOutputStream fop;
        try {
            fop = new FileOutputStream(file);
            if (!file.exists()) {
                boolean exists = file.createNewFile();
                if (!exists) System.out.println("[ERROR] Cannot save the game.");
            }

            byte[] contentInBytes = data.getBytes();
            fop.write(contentInBytes);
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot save the game into " + curNum + ".sgf");
        }
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
