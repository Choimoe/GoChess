package GoScene;

import GoGame.GoMain;
import GoGame.GoStep;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.List;
import java.util.Objects;

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

    int pieceCount = 0;
    ImageView[] pieceList = new ImageView[BOARD_ROW * BOARD_COL];

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

    /**
     * reshapeImageWithHeight: change the height of the image by *keeping ratio*
     * @param imageView: the image waiting change
     * @param height: the height of the image
     */
    public void reshapeImageWithHeight(ImageView imageView, int height) {
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(height);
    }

    /**
     * loadImage: load the piece and board image.
     *  - assets/blackPieceWait.png
     *  - assets/whitePieceWait.png
     *  - assets/blackPiece.png
     *  - assets/whitePiece.png
     *  - assets/board.png
     */
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

    /**
     * setPiecePosition: display the piece
     * @param piece: the image waiting to view
     * @param posX,posY: the *absolute* position of image
     */
    public void setPiecePosition (ImageView piece, int posX, int posY) {
        reshapeImageWithHeight(piece, 2 * RADIUS - 1);
        piece.setX(getAbsolutePosX(posX) - RADIUS + 1);
        piece.setY(getAbsolutePosY(posY) - RADIUS + 1);
    }

    /**
     * newPieceImage: make a piece with image
     * @param type: the type of piece
     * @param posX,posY: the *absolute* position of image
     * @return the object of the piece
     */
    public ImageView newPieceImage (int type, int posX, int posY) {
        ImageView newPiece = new ImageView(pieceImage[type]);
        setPiecePosition(newPiece, posX, posY);
        return newPiece;
    }

    /**
     * newPieceImage: make a waiting piece with image
     * @param type: the type of waiting piece
     * @return the object of the piece
     */
    public ImageView newPieceWaitImage (int type) {
        ImageView newPiece = new ImageView(pieceWaitImage[type]);
        reshapeImageWithHeight(newPiece, 2 * RADIUS - 1);
        newPiece.setVisible(false);
        return newPiece;
    }

    /**
     * setPane: initialize the pane
     * add the board with image to the pane
     * set the mouse action about moving and clicking
     */
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

    /**
     * clear: reset all the data
     */
    public void clear() {
        goGame.clear();

        pane.getChildren().clear();
        goGame.beginGame();

        setPane();
    }

    /**
     * removePieceDisplay: remove the piece from the pane
     * @param boardX,boardY: the *board* position which we can get to locate the piece, belongs to [0, 18]
     */
    protected void removePieceDisplay(int boardX, int boardY) {
        int step = goGame.getPosStep(boardX, boardY);
        if (step == 0) return;
        System.out.println("delete: " + pieceList[step]);
        pane.getChildren().remove(pieceList[step]);
    }

    /**
     * recoverPieces: recover all the piece from steps array(GoStep[]) and display them
     */
    public void recoverPieces() {
        pane.getChildren().clear();
        setPane();
        pieceCount = 0;
        GoStep[] steps = goGame.getGoSteps();
        for (GoStep step : steps) {
            if (step == null) continue;
            int boardPosX = step.getX(), boardPosY = step.getY(), player = step.getPlayer();

            ImageView newPiece = newPieceImage(player, boardPosX, boardPosY);
            pane.getChildren().add(pieceList[++pieceCount] = newPiece);
        }

        pane.getChildren().remove(pieceWaitDisplay);
        pieceWaitDisplay = newPieceWaitImage(goGame.getCurrentPlayer());
        pane.getChildren().add(pieceWaitDisplay);
    }

    /**
     * updatePieces: delete the pieces which do not have "Chi"
     * @param list: the piece waiting to be removed
     */
    private void updatePieces(List<GoStep> list) {
        if (list == null) return;
        for (GoStep step : list) {
            removePieceDisplay(step.getX(), step.getY());
        }
    }

    /**
     * setPiece: try to put the piece
     * @param posX,posY: the *absolute* position of image
     */
    public void setPiece(double posX, double posY) {
        int boardPosX = getBoardPosX(posX), boardPosY = getBoardPosY(posY);
        if (boardPosX == -1 || boardPosY == -1) return;

        /* try to put the piece */
        if (goGame.putPiece(boardPosX, boardPosY)) {
            /* make the new pieces */
            ImageView newPiece = newPieceImage(goGame.getLastPlayer(), boardPosX, boardPosY);
            pane.getChildren().add(pieceList[++pieceCount] = newPiece);

            /* set the display of the deleted pieces */
            List<GoStep> list = goGame.getRemovePieces();
            updatePieces(list);
            goGame.removePiece(list);

            /* reset the display of the waiting piece */
            pane.getChildren().remove(pieceWaitDisplay);
            pieceWaitDisplay = newPieceWaitImage(goGame.getCurrentPlayer());
            pane.getChildren().add(pieceWaitDisplay);
        }
    }

    /**
     * setPieceWait: display the waiting piece on the given position
     * @param posX,posY: the *absolute* position of image
     */
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
        initialButton(new String[]{"认输", "读档", "存档", "退出"}, 170, 80);

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

        /* set the "读档" action */
        setButtonAction(1, () -> {
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
        setButtonAction(2, () -> {
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