import GoScene.BoardPage;
import GoScene.HomePage;
import GoScene.StatGamePage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    int width = 1366;
    int height = 768;
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Go");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.initStyle(StageStyle.TRANSPARENT);

        HomePage homePage = new HomePage();
        StatGamePage startPage = new StatGamePage();
        BoardPage chessBoard = new BoardPage();

        stage.setScene(homePage.getScene());

        homePage.button[2].button.setOnAction((ActionEvent e) -> stage.close());
        homePage.setButtonJump(stage, 0, startPage.getScene());
        startPage.setButtonJump(stage, 2, homePage.getScene());
        startPage.setButtonJump(stage, 0, chessBoard.getScene());
        chessBoard.setButtonJump(stage, 3, startPage.getScene(), chessBoard::clear);

        stage.show();
        homePage.setDragged(stage);
        startPage.setDragged(stage);
        chessBoard.setDragged(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}