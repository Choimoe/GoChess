package GoScene;

import GoBoard.ChessBoard;
import GoDataIO.InputData;
import GoServer.GoClient;
import GoUtil.GoLogger;
import Identify.IdentifyLinker;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainControl extends Application {
    /* the size of the main window */
    int width = 1366, height = 768;
    final int PAGE_NUMBER = 4;

    InputData fileData;
    GoClient client;
    Thread clientThread;

    ChessBoard board;

    ButtonPages[] pages = new ButtonPages[PAGE_NUMBER];

    int gameCount = 0;

    /**
     * initialize the style:
     *  - title: "Go"
     *  - width: this.width
     *  - height: this.height
     * @param stage: change the style of this stage
     */
    void initializeStageStyle(Stage stage) {
        stage.setTitle("Go");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.initStyle(StageStyle.TRANSPARENT);
    }

    void createPages() {
        HomePage homePage       = new HomePage();
        StartGamePage startPage = new StartGamePage();
        BoardPage chessBoard    = new BoardPage(fileData, client, clientThread, new String[]{"认输", "虚着", "回想", "复盘", "存档", "离开"});
        GoReview reviewBoard   = new GoReview(chessBoard);

        board = chessBoard.getChessBoard();

        pages[0] = homePage;
        pages[1] = startPage;
        pages[2] = chessBoard;
        pages[3] = reviewBoard;
    }


    void setJumpRelation(Stage stage) {
        IdentifyLinker linker = new IdentifyLinker();
        ButtonPages homePage    = pages[0];
        ButtonPages startPage   = pages[1];
        ButtonPages chessBoard  = pages[2];
        ButtonPages review      = pages[3];

        homePage.button[2].button.setOnAction((ActionEvent e) -> {
            fileData.release();
            stage.close();
            if (clientThread != null) clientThread.interrupt();
        });

        homePage.setButtonJump      (stage, 0, startPage .getScene());
        review.setButtonJump        (stage, 1, startPage .getScene(), () -> {
            linker.setRunning(false);
        });

        startPage.setButtonJump     (stage, 0, chessBoard.getScene(), () -> startPlay(chessBoard, true ));
        startPage.setButtonJump     (stage, 1, chessBoard.getScene(), () -> startPlay(chessBoard, false));
        startPage.setButtonJump     (stage, 2, review    .getScene(), () -> new Thread(() -> {
            try {
                linker.linker(((GoReview) review).getBoard());
            } catch (IOException | InterruptedException e) {
                GoLogger.error("Cannot Start Running Linker");
                throw new RuntimeException(e);
            }
        }).start());
        startPage.setButtonJump     (stage, 3, homePage  .getScene());

        chessBoard.setButtonJump    (stage, 5, startPage .getScene(), () -> {
            chessBoard.clear();
            client.request("exit");
        });
    }

    private void startPlay(ButtonPages chessBoard, boolean isLocalGame) {
        chessBoard.createClient();
        if (!((BoardPage) chessBoard).getChessBoard().checkFirstTime()) {
            ((BoardPage) chessBoard).getChessBoard().clear();
        }
        GoLogger.debug("isLocalGame = " + isLocalGame);
        client.setLocalGame(isLocalGame);
        client.setBoard(board);
        clientThread = new Thread(() -> client.run());
        clientThread.start();
        if (gameCount++ > 0) client.request("gameStart");
    }

    Scene getMainScene() {
        return pages[0].getScene();
    }

    void setPagesDragged(Stage stage) {
        for (ButtonPages page : pages)
            if (page != null) page.setDragged(stage);
    }

    @Override
    public void start(Stage stage) throws Exception {
        fileData = new InputData();
        client = new GoClient();

        initializeStageStyle(stage);

        createPages();

        stage.setScene(getMainScene());

        setJumpRelation(stage);

        stage.show();

        setPagesDragged(stage);
    }

    public void display() {
        launch();

        GoLogger.debug("display() end.");
    }

    public void clean() {
        for (ButtonPages page : pages) {
            if (page == null) continue;
            page.cleanPages();
        }
    }
}