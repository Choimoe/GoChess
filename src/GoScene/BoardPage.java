package GoScene;

import GoBoard.ChessBoard;
import GoDataIO.InputData;
import GoServer.GoClient;
import GoUtil.GoLogger;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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

        Group group         = new Group();
        rootPane            = new BorderPane(group);
        boardThread         = new Thread(board);
        scene               = new Scene(rootPane);
        board               = new ChessBoard(data);

        VBox buttonLayout   = new VBox();
        HBox infoLayout     = new HBox();
        HBox nextButLayout  = new HBox();

        boardThread.start();
        board.setClient(client);

        /* set the layout of the button */
        buttonLayout.setLayoutX(1175);
        buttonLayout.setLayoutY(10  );
        buttonLayout.setSpacing(20  );

        nextButLayout.setLayoutX(855);
        nextButLayout.setLayoutY(675);

        infoLayout.setLayoutX(855);
        infoLayout.setLayoutY(10);

        Text text = new Text("");
        board.setInfoTextDisplay(text);
        text.setFontSmoothingType(FontSmoothingType.GRAY);
        text.setFont(Font.font("Arial", 20));
        infoLayout.getChildren().add(text);

        /* put the button one the pane */
        for (int i = 0; i < buttonNumber; i++)
            buttonLayout.getChildren().add(button[i].getButton());

        CustomButton nextPlace = new CustomButton("下一步", 300, 70);
        nextButLayout.getChildren().add(nextPlace.getButton());

        /* put the button and pane on the rootPane */
        group.getChildren().add(board.getPane());
        group.getChildren().add(buttonLayout);
        group.getChildren().add(nextButLayout);
        group.getChildren().add(infoLayout);

        setButtonAction(1, () -> board.requestSkipTurn());

        setButtonAction(nextPlace.getButton(), () -> board.stepByStepRecover());
        setButtonAction("回想", () -> readGoFromSave(inputData, client));
        setButtonAction("复盘", () -> readGoFromAnalysis(inputData, client));
        setButtonAction("存档", () -> saveGo(inputData));
    }

    private void readGoFromAnalysis(InputData inputData, GoClient client) {
        if (inputData.getSavesCount() == 0) {
            GoLogger.error("No saves found.");
            return;
        }

        inputData.refreshReadSave();
        String gameData = inputData.getGoGame(301);
        board.addRequest(client.request("anaSave", gameData));
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
