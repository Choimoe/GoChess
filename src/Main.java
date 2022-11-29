/**
 * repo: https://github.com/Choimoe/GoChess
 */

import GoScene.BoardPage;
import GoScene.HomePage;
import GoScene.StartGamePage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    /* the size of the main window */
    int width = 1366, height = 768;

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

    @Override
    public void start(Stage stage) throws Exception {
        initializeStageStyle(stage);

        HomePage homePage = new HomePage();
        StartGamePage startPage = new StartGamePage();
        BoardPage chessBoard = new BoardPage();

        stage.setScene(homePage.getScene());

        /* set the jump relation */
        homePage.button[2].button.setOnAction((ActionEvent e) -> stage.close());
        homePage.setButtonJump(stage, 0, startPage.getScene());
        startPage.setButtonJump(stage, 2, homePage.getScene());
        startPage.setButtonJump(stage, 0, chessBoard.getScene());
        chessBoard.setButtonJump(stage, 3, startPage.getScene(), chessBoard::clear);

        stage.show();

        /* set the dragged */
        homePage.setDragged(stage);
        startPage.setDragged(stage);
        chessBoard.setDragged(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}