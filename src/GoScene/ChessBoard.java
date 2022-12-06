package GoScene;

import GoGame.GoMain;
import GoGame.GoStep;
import GoSound.SoundList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

public class ChessBoard implements Serializable {
    Pane        pane;
    GoMain      goGame;
    SoundList   sound;

    final int       BOARD_ROW = 19, BOARD_COL = 19;

    /* the NW and SE position of the image */
    final double    NW_X = 51, NW_Y = 45, SE_X = 779, SE_Y = 722;

    /* the X and Y length of the block */
    final double    LEN_X = (SE_X - NW_X) / (BOARD_ROW - 1.0),
                    LEN_Y = (SE_Y - NW_Y) / (BOARD_COL - 1.0);

    /* the radius of the piece */
    final int       RADIUS = (int)((LEN_X * 0.9) * 0.5);

    /* the input image */
    Image[]     pieceWaitImage      = new Image[3];
    Image[]     pieceImage          = new Image[3];
    ImageView   boardImageView      = new ImageView();
    ImageView   pieceWaitDisplay    = new ImageView();
    ImageView[] pieceWait           = new ImageView[3];
    ImageView[] piece               = new ImageView[3];
    ImageView[] pieceList           = new ImageView[BOARD_ROW * BOARD_COL];

    int pieceCount = 0;

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
        if (imageView == null) return;
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
        pieceWaitImage[1]   = new Image(new FileInputStream("assets/blackPieceWait.png"));
        pieceWaitImage[2]   = new Image(new FileInputStream("assets/whitePieceWait.png"));
        pieceImage[1]       = new Image(new FileInputStream("assets/blackPiece.png"));
        pieceImage[2]       = new Image(new FileInputStream("assets/whitePiece.png"));

        pieceWait[1]        = new ImageView(pieceWaitImage[1]);
        pieceWait[2]        = new ImageView(pieceWaitImage[2]);
        piece[1]            = new ImageView(pieceImage[1]);
        piece[2]            = new ImageView(pieceImage[2]);

        for (int i = 1; i <= 2; i++) reshapeImageWithHeight(pieceWait[i], 2 * RADIUS - 1);
        for (int i = 1; i <= 2; i++) reshapeImageWithHeight(piece[i]    , 2 * RADIUS - 1);

        boardImageView = new ImageView(new Image(new FileInputStream("assets/board.png")));

        reshapeImageWithHeight(boardImageView, 768);
    }

    /**
     * loadImage: load the sound.
     *  - assets/putPiece.wav
     */
    public void loadSound() {
        String[] audioPath = {"assets\\putPiece.wav"};
        sound = new SoundList(audioPath);
    }

    /**
     * setPiecePosition: display the piece
     * @param piece: the image waiting to view
     * @param posX: the *absolute* x position
     * @param posY: the *absolute* y position
     */
    public void setPiecePosition (ImageView piece, int posX, int posY) {
        reshapeImageWithHeight(piece, 2 * RADIUS - 1);

        piece.setX(getAbsolutePosX(posX) - RADIUS + 1);
        piece.setY(getAbsolutePosY(posY) - RADIUS + 1);
    }

    /**
     * newPieceImage: make a piece with image
     * @param type: the type of piece
     * @param posX: the *absolute* x position
     * @param posY: the *absolute* y position
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

        pane.setOnMouseMoved    (event -> setPieceWait  (event.getX(), event.getY()));
        pane.setOnMouseClicked  (event -> setPiece      (event.getX(), event.getY()));
    }

    public ChessBoard() throws FileNotFoundException {
        loadImage();
        loadSound();

        pane    = new Pane();
        goGame  = new GoMain();

        beginGoGame();

        setPane();
    }

    /**
     * Start the game
     * TODO: there should do some action
     *  - play audio
     *  - reset the timer
     */
    void beginGoGame() {
        goGame.beginGame();
    }

    /**
     * clear: reset all the data
     */
    public void clear() {
        goGame.clear();

        pane.getChildren().clear();

        beginGoGame();

        setPane();
    }

    /**
     * changeWaitingDisplay: set pieceWaitDisplay to the other side
     */
    public void updateWaitingDisplay() {
        pane.getChildren().remove(pieceWaitDisplay);
        pieceWaitDisplay = newPieceWaitImage(goGame.getCurrentPlayer());
        pane.getChildren().add(pieceWaitDisplay);
    }

    /**
     * skipTurn
     */
    public void skipTurn() {
        goGame.skipTurn();
        pieceCount++;
        updateWaitingDisplay();
    }

    /**
     * removePieceDisplay: remove the piece from the pane
     * @param boardX: the *board* x position
     * @param boardY: the *board* y position
     */
    protected void removePieceDisplay(int boardX, int boardY) {
        int step = goGame.getPosStep(boardX, boardY);

        if (step == 0)                  return;
        if (pieceList[step] == null)    return;

        pane.getChildren().remove(pieceList[step]);
        pieceList[step] = null;
    }

    /**
     * recoverPieces: recover all the piece from steps array(GoStep[]) and display them
     */
    public void recoverPieces() {
        pane.getChildren().clear();
        setPane();
        pieceCount = 0;
        List<GoStep> steps = goGame.getGoSteps();
        for (GoStep step : steps) {
            if (step == null) continue;
            if ((step.getX() == -1) || (step.getY() == -1) || (step.getPlayer() == -1)) continue;

            int boardPosX = step.getX(), boardPosY = step.getY(), player = step.getPlayer();

            ImageView newPiece = newPieceImage(player, boardPosX, boardPosY);
            pieceList[goGame.getPosStep(boardPosX, boardPosY)] = newPiece;
            pane.getChildren().add(newPiece);
        }

        pieceCount = goGame.getSteps();

        updateWaitingDisplay();
    }

    /**
     * updatePieces: delete the pieces which do not have "Chi"
     * @param list: the piece waiting to be removed
     */
    private void updatePieces(List<GoStep> list) {
        if (list == null) return;
        list.forEach(step -> removePieceDisplay(step.getX(), step.getY()));
    }

    /**
     * setPiece: try to put the piece
     * @param posX: the *absolute* x position
     * @param posY: the *absolute* y position
     */
    public void setPiece(double posX, double posY) {
        int boardPosX = getBoardPosX(posX), boardPosY = getBoardPosY(posY);
        if (boardPosX == -1 || boardPosY == -1) return;

        /* try to put the piece */
        if (goGame.putPiece(boardPosX, boardPosY)) {
            /* play putPiece sound */
            sound.play(0);

            /* make the new pieces */
            ImageView newPiece = newPieceImage(goGame.getLastPlayer(), boardPosX, boardPosY);
            pane.getChildren().add(pieceList[++pieceCount] = newPiece);

            /* set the display of the deleted pieces */
            List<GoStep> list = goGame.getRemovePieces(boardPosX, boardPosY);
            updatePieces(list);
            goGame.removePiece(list);

            /* reset the display of the waiting piece */
            updateWaitingDisplay();
        }
    }

    /**
     * setPieceWait: display the waiting piece on the given position
     * @param posX: the *absolute* x position
     * @param posY: the *absolute* y position
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

    void clean() {
        sound.recycle();
    }
}