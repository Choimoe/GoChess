package GoScene;

import GoGame.GoMain;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

class ChessBoard implements Serializable {
    ImageView pieceWaitDisplay;
    Pane pane;

    GoMain goGame;

    final int BOARD_ROW = 19, BOARD_COL = 19;
    final double NW_X = 51, NW_Y = 45, SE_X = 779, SE_Y = 722;
    final double LEN_X = (SE_X - NW_X) / (BOARD_ROW - 1.0),
            LEN_Y = (SE_Y - NW_Y) / (BOARD_COL - 1.0);
    final int RADIUS = (int)((LEN_X * 0.9) * 0.5);

    Image[] pieceWaitImage = new Image[3];
    Image[] pieceImage = new Image[3];

    ImageView boardImageView = new ImageView();
    ImageView[] pieceWait = new ImageView[3];
    ImageView[] piece = new ImageView[3];

    public Pane getPane() { return pane; }

    final double abs(double x) { return x < 0 ? -x : x; }

    final int getBoardPosX (double posX) {
        double divided = (posX - NW_X) / LEN_X;
        int getInt = (int)(divided + 0.5);
        if (abs(divided - getInt) > 0.3 || getInt >= 19) return -1;
        else return getInt;
    }
    final int getBoardPosY (double posY) {
        double divided = (posY - NW_Y) / LEN_Y;
        int getInt = (int)(divided + 0.5);
        if (abs(divided - getInt) > 0.3 || getInt >= 19) return -1;
        else return getInt;
    }

    final int getAbsolutePosX (int posX) { return (int)(posX * LEN_X + NW_X); }
    final int getAbsolutePosY (int posY) { return (int)(posY * LEN_Y + NW_Y); }

    public void reshapeImageWithHeight(ImageView imageView, int height) {
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(height);
    }

    public void loadImage() throws FileNotFoundException {
        FileInputStream input;
        input = new FileInputStream("assets/blackPieceWait.png");
        pieceWaitImage[1] = new Image(input);
        pieceWait[1] = new ImageView(pieceWaitImage[1]);
        input = new FileInputStream("assets/whitePieceWait.png");
        pieceWaitImage[2] = new Image(input);
        pieceWait[2] = new ImageView(pieceWaitImage[2]);

        input = new FileInputStream("assets/blackPiece.png");
        pieceImage[1] = new Image(input);
        piece[1] = new ImageView(pieceImage[1]);
        input = new FileInputStream("assets/whitePiece.png");
        pieceImage[2] = new Image(input);
        piece[2] = new ImageView(pieceImage[2]);

        for (int i = 1; i <= 2; i++) reshapeImageWithHeight(pieceWait[i], 2 * RADIUS - 1);
        for (int i = 1; i <= 2; i++) reshapeImageWithHeight(piece[i]    , 2 * RADIUS - 1);

        input = new FileInputStream("assets/board.png");
        boardImageView = new ImageView(new Image(input));
        reshapeImageWithHeight(boardImageView, 768);
    }

    public void setPiecePosition (ImageView piece, int posX, int posY) {
        reshapeImageWithHeight(piece, 2 * RADIUS - 1);
        piece.setX(getAbsolutePosX(posX) - RADIUS + 1);
        piece.setY(getAbsolutePosY(posY) - RADIUS + 1);
    }

    public ImageView newPieceImage (int type, int posX, int posY) {
        ImageView newPiece = new ImageView(pieceImage[type]);
        setPiecePosition(newPiece, posX, posY);
//        System.out.println(posX + " " + posY);
        return newPiece;
    }

    public ImageView newPieceWaitImage (int type) {
        ImageView newPiece = new ImageView(pieceWaitImage[type]);
        reshapeImageWithHeight(newPiece, 2 * RADIUS - 1);
        newPiece.setVisible(false);
        return newPiece;
    }

    public void setPane() {
        pieceWaitDisplay = pieceWait[goGame.getCurrentPlayer()];
        pieceWaitDisplay.setVisible(false);

        pane.getChildren().add(boardImageView);
        pane.getChildren().add(pieceWaitDisplay);

        boardImageView.setPreserveRatio(true);

        pane.setOnMouseMoved(event -> setPieceWait(event.getX(), event.getY()));
        pane.setOnMouseClicked(event -> setPiece(event.getX(), event.getY()));
    }

    public ChessBoard() throws FileNotFoundException {
        loadImage();

        pane = new Pane();
        goGame = new GoMain();
        goGame.beginGame();

        setPane();
    }

    public void clear() {
        goGame.clear();

        pane.getChildren().clear();
        goGame.beginGame();

        setPane();
    }

    public void setPiece(double posX, double posY) {
        int boardPosX = getBoardPosX(posX), boardPosY = getBoardPosY(posY);
        if (boardPosX == -1 || boardPosY == -1) return;
        if (goGame.putPiece(boardPosX, boardPosY)) {
//            System.out.println("Put a piece at " + boardPosX + " " + boardPosY);
            pane.getChildren().add(newPieceImage(goGame.getLastPlayer(), boardPosX, boardPosY));

            pane.getChildren().remove(pieceWaitDisplay);
            pieceWaitDisplay = newPieceWaitImage(goGame.getCurrentPlayer());
            pane.getChildren().add(pieceWaitDisplay);
        }
    }

    public void setPieceWait(double posX, double posY) {
        int boardPosX = getBoardPosX(posX), boardPosY = getBoardPosY(posY);
        if (boardPosX == -1 || boardPosY == -1) {
            pieceWaitDisplay.setVisible(false);
            return;
        }

        if (!goGame.isEmpty(boardPosX, boardPosY)) {
            pieceWaitDisplay.setVisible(false);
            return;
        }

        pieceWaitDisplay.setVisible(true);
        pieceWaitDisplay.setX(getAbsolutePosX(boardPosX) - RADIUS + 1);
        pieceWaitDisplay.setY(getAbsolutePosY(boardPosY) - RADIUS + 1);
    }
}

public class BoardPage extends ButtonPages{
    ChessBoard board;
    BorderPane rootPane;

    public BoardPage() throws FileNotFoundException {
        initialButton(170, 80, new String[]{"认输", "和棋", "存档", "强制退出"});

        board = new ChessBoard();

        rootPane = new BorderPane();
        scene = new Scene(rootPane);

        VBox buttonLayout = new VBox();

        buttonLayout.setLayoutX(1100);
        buttonLayout.setLayoutY(350);
        buttonLayout.setSpacing(20);

        for (int i = 0; i < buttonNumber; i++)
            buttonLayout.getChildren().add(button[i].getButton());

        rootPane.setRight(buttonLayout);
        rootPane.setLeft(board.getPane());

        setButtonAction(2, () -> {
        });
    }

    public void clear() {
        board.clear();
    }
}