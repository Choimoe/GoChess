package GoScene;

import GoBoard.ChessBoard;
import GoDataIO.InputData;
import GoServer.GoClient;
import GoUtil.GoLogger;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.*;

public class BoardPage extends ButtonPages {
    ChessBoard board;
    BorderPane rootPane;

    InputData data;

    GoClient client;
    Thread clientThread, boardThread;

    /**
     * set all the object on the page
     */
    public BoardPage(InputData inputData, GoClient client, Thread clientThread, String[] buttonNames) {
        initialButton(buttonNames, 170, 80);

        this.data           = inputData;
        this.client         = client;
        this.clientThread   = clientThread;

        rootPane            = new BorderPane();
        boardThread         = new Thread(board);
        scene               = new Scene(rootPane);
        board               = new ChessBoard(data);

        VBox buttonLayout   = new VBox();

        boardThread.start();
        board.setClient(client);

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

        setButtonAction("回想", () -> readGoFromSave(inputData, client));
        setButtonAction("存档", () -> saveGo(inputData));
    }

    private void readGoFromAnalysis(InputData inputData) {
    }

    private void readGoFromSave(InputData inputData, GoClient client) {
        if (inputData.getSavesCount() == 0) {
            GoLogger.error("No saves found.");
            return;
        }

        inputData.refreshReadSave();
        String gameData = inputData.getGoGame(301);
        board.addRequest(client.request("loadSave", gameData));
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
                if (!exists) GoLogger.error("Cannot save the game.");
            }

            byte[] contentInBytes = data.getBytes();
            fop.write(contentInBytes);
        } catch (IOException e) {
            GoLogger.error("Cannot save the game into " + curNum + ".sgf");
        }
    }

    public Thread getThread() {
        return boardThread;
    }

    public ChessBoard getChessBoard() {
        return board;
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
