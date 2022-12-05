package GoScene;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainControl extends Application {
    /* the size of the main window */
    int width = 1366, height = 768;
    final int PAGE_NUMBER = 3;

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

    void createPages() throws Exception {
        HomePage homePage = new HomePage();
        StartGamePage startPage = new StartGamePage();
        BoardPage chessBoard = new BoardPage();

        pages[0] = homePage;
        pages[1] = startPage;
        pages[2] = chessBoard;
    }

    void setJumpRelation(Stage stage) {
        ButtonPages homePage = pages[0];
        ButtonPages startPage = pages[1];
        ButtonPages chessBoard = pages[2];

        homePage.button[2].button.setOnAction((ActionEvent e) -> stage.close());

        homePage.setButtonJump(stage, 0, startPage.getScene());
        startPage.setButtonJump(stage, 2, homePage.getScene());
        startPage.setButtonJump(stage, 0, chessBoard.getScene());
        chessBoard.setButtonJump(stage, 3, startPage.getScene(), chessBoard::clear);
    }

    Scene getMainScene() {
        return pages[0].getScene();
    }

    void setPagesDragged(Stage stage) {
        for (ButtonPages page : pages) page.setDragged(stage);
    }

    @Override
    public void start(Stage stage) throws Exception {
        initializeStageStyle(stage);

        createPages();

        stage.setScene(getMainScene());

        setJumpRelation(stage);

        stage.show();

        setPagesDragged(stage);
    }

    public void display() {
        launch();
    }
}