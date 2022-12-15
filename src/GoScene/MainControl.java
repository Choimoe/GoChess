package GoScene;

import GoDataIO.InputData;
import GoServer.GoClient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainControl extends Application {
    /* the size of the main window */
    int width = 1366, height = 768;
    final int PAGE_NUMBER = 3;

    InputData fileData;
    GoClient client;
    Thread clientThread;

    ButtonPages[] pages = new ButtonPages[PAGE_NUMBER];

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
        BoardPage chessBoard    = new BoardPage(fileData, client, clientThread);

        pages[0] = homePage;
        pages[1] = startPage;
        pages[2] = chessBoard;
    }


    void setJumpRelation(Stage stage) {
        ButtonPages homePage    = pages[0];
        ButtonPages startPage   = pages[1];
        ButtonPages chessBoard  = pages[2];

        homePage.button[2].button.setOnAction((ActionEvent e) -> {
            fileData.release();
            stage.close();
            if (clientThread != null) clientThread.interrupt();
        });

        homePage.setButtonJump      (stage, 0, startPage .getScene());
        startPage.setButtonJump     (stage, 3, homePage  .getScene());
        startPage.setButtonJump     (stage, 0, chessBoard.getScene(), () -> {
            chessBoard.createClient();
            clientThread = new Thread(() -> client.run());
            clientThread.start();
        });
        chessBoard.setButtonJump    (stage, 4, startPage .getScene(), chessBoard::clear);
    }

    Scene getMainScene() {
        return pages[0].getScene();
    }

    void setPagesDragged(Stage stage) {
        for (ButtonPages page : pages) page.setDragged(stage);
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

        System.out.println("[DEBUG] display() end.");
    }

    public void clean() {
        for (ButtonPages page : pages) {
            if (page == null) continue;
            page.cleanPages();
        }
    }
}