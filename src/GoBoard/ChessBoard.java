package GoBoard;

import GoDataIO.InputData;
import GoGame.GoMain;
import GoGame.GoStep;
import GoServer.GoClient;
import GoSound.SoundList;

import GoUtil.GoLogger;
import GoUtil.ImageUtil;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static javafx.application.Platform.runLater;

public class ChessBoard implements Serializable, Runnable{
    Pane        pane;
    GoMain      goGame;
    GoClient    client;
    SoundList   sound;

    final int       BOARD_ROW = 19, BOARD_COL = 19;

    /* the NW and SE position of the image */
    final double    NW_X = 51, NW_Y = 45, SE_X = 779, SE_Y = 722;

    /* the X and Y length of the block */
    final double    LEN_X = (SE_X - NW_X) / (BOARD_ROW - 1.0),
                    LEN_Y = (SE_Y - NW_Y) / (BOARD_COL - 1.0);

    /* the radius of the piece */
    final int       RADIUS = (int)((LEN_X * 0.9) * 0.5);

    InputData inputData;

    boolean isRunning = false, isReadOnly = false;
    Queue<Integer> requestID = new LinkedList<>();

    /* the input image */
    Image[]     pieceWaitImage      = new Image[3];
    Image[]     pieceImage          = new Image[3];
    ImageView   boardImageView      = new ImageView();
    ImageView   pieceWaitDisplay    = new ImageView();
    ImageView[] pieceWait           = new ImageView[3];
    ImageView[] piece               = new ImageView[3];
    ImageView[] pieceList           = new ImageView[BOARD_ROW * BOARD_COL * 3];

    int pieceCount = 0;
    int requestTimes = 0;

    public Pane getPane() { return pane; }
    public int getPlayer() { return goGame.getCurrentPlayer(); }
    public void addRequest(int requestID) { this.requestID.add(requestID); }

    /**
     * loadImage: load the piece and board image.
     *  - 101:assets/blackPieceWait.png
     *  - 102:assets/whitePieceWait.png
     *  - 103:assets/blackPiece.png
     *  - 104:assets/whitePiece.png
     *  - 105:assets/board.png
     */
    public void loadImage() {
        pieceWaitImage[1]   = inputData.getImage(101);
        pieceWaitImage[2]   = inputData.getImage(102);
        pieceImage[1]       = inputData.getImage(103);
        pieceImage[2]       = inputData.getImage(104);

        pieceWait[1]        = new ImageView(pieceWaitImage  [1]);
        pieceWait[2]        = new ImageView(pieceWaitImage  [2]);
        piece[1]            = new ImageView(pieceImage      [1]);
        piece[2]            = new ImageView(pieceImage      [2]);

        for (int i = 1; i <= 2; i++) ImageUtil.reshapeImageWithHeight(pieceWait[i], 2 * RADIUS - 1);
        for (int i = 1; i <= 2; i++) ImageUtil.reshapeImageWithHeight(piece[i]    , 2 * RADIUS - 1);

        boardImageView = new ImageView(inputData.getImage(105));

        ImageUtil.reshapeImageWithHeight(boardImageView, 768);
    }

    /**
     * loadImage: load the sound.
     *  - assets/putPiece.wav
     */
    public void loadSound() {
        sound = new SoundList(inputData.getData());
    }

    /**
     * setPiecePosition: display the piece
     * @param piece: the image waiting to view
     * @param posX: the *absolute* x position
     * @param posY: the *absolute* y position
     */
    public void setPiecePosition (ImageView piece, int posX, int posY) {
        ImageUtil.reshapeImageWithHeight(piece, 2 * RADIUS - 1);

        piece.setX(ImageUtil.getAbsolutePosX(posX, NW_X, LEN_X) - RADIUS + 1);
        piece.setY(ImageUtil.getAbsolutePosY(posY, NW_Y, LEN_Y) - RADIUS + 1);
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
        ImageUtil.reshapeImageWithHeight(newPiece, 2 * RADIUS - 1);
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

        runLater(() -> {
            pane.getChildren().add(boardImageView);
            pane.getChildren().add(pieceWaitDisplay);
        });

        boardImageView.setPreserveRatio(true);

        pane.setOnMouseMoved    (event -> setPieceWait  (event.getX(), event.getY()));
        pane.setOnMouseClicked  (event -> setPiece      (event.getX(), event.getY()));
    }

    public void setClient(GoClient client) {
        this.client = client;
    }

    public ChessBoard(InputData inputData) {
        this.inputData = inputData;

        loadImage();
        loadSound();

        pane    = new Pane();
        goGame  = new GoMain();

        beginGoGame();

        setPane();

        isRunning = true;
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
        pieceCount = 0;
        goGame.clear();
        pane.getChildren().clear();
        beginGoGame();
        setPane();
    }

    /**
     * changeWaitingDisplay: set pieceWaitDisplay to the other side
     */
    public void updateWaitingDisplay() {
        runLater(() -> {
            pane.getChildren().remove(pieceWaitDisplay);
            GoLogger.debug("change to " + goGame.getCurrentPlayer());
            pieceWaitDisplay = newPieceWaitImage(goGame.getCurrentPlayer());
            pane.getChildren().add(pieceWaitDisplay);
        });
    }

    public void skipTurn(char status) {
        if (status == 'F') return;
        goGame.skipTurn();
        pieceCount++;
        updateWaitingDisplay();
    }

    public void requestSkipTurn() {
        requestID.add(client.request("skipTurn", getPlayer()));
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

        runLater(() -> {
            pane.getChildren().remove(pieceList[step]);
            pieceList[step] = null;
        });
    }

    /**
     * recoverPieces: recover all the piece from steps array(GoStep[]) and display them
     */
    public void recoverPieces(String goGameData) {
        runLater(() -> pane.getChildren().clear());
        setPane();
        pieceCount = 0;
        goGame.recover(goGameData);
        List<GoStep> steps = goGame.getGoSteps();
        for (GoStep step : steps) {
            if (step == null) continue;
            if ((step.getX() == -1) || (step.getY() == -1) || (step.getPlayer() == -1)) continue;

            int boardPosX = step.getX(), boardPosY = step.getY(), player = 2 - (step.getPlayer() & 1);

//            System.out.println("[DEBUG] Recover: " + boardPosX + " " + boardPosY + " " + player);
            putPieceFromRecover(boardPosX, boardPosY, player);
        }

        pieceCount = goGame.getSteps();

        updateWaitingDisplay();
    }

    private void putPieceFromRecover(int boardPosX, int boardPosY, int player) {
        ImageView newPiece = newPieceImage(player, boardPosX, boardPosY);
        pieceList[goGame.getPosStep(boardPosX, boardPosY)] = newPiece;
        runLater(() -> pane.getChildren().add(newPiece));
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
        int boardPosX = ImageUtil.getBoardPosX(posX, NW_X, LEN_X),
            boardPosY = ImageUtil.getBoardPosY(posY, NW_Y, LEN_Y);

        if (boardPosX == -1 || boardPosY == -1) return;

        requestID.add(client.request("putPiece", boardPosX, boardPosY, getPlayer()));
    }

    private void setPieceDisplay(int boardPosX, int boardPosY) {
        /* try to put the piece */
        if (goGame.putPiece(boardPosX, boardPosY)) {
            /* play putPiece sound */
            sound.play(201);

            /* make the new pieces */
            ImageView newPiece = newPieceImage(goGame.getLastPlayer(), boardPosX, boardPosY);
            runLater(() -> pane.getChildren().add(pieceList[++pieceCount] = newPiece));

            /* set the display of the deleted pieces */
            List<GoStep> list = goGame.getRemovePieces(boardPosX, boardPosY);
            updatePieces(list);
            goGame.removePiece(list);

            /* reset the display of the waiting piece */
            updateWaitingDisplay();
        }
    }

    public void forceSetPieceDisplay(int x, int y) {
        setPieceDisplay(x, y);
    }

    /**
     * setPieceWait: display the waiting piece on the given position
     * @param posX: the *absolute* x position
     * @param posY: the *absolute* y position
     */
    public void setPieceWait(double posX, double posY) {
        int boardPosX = ImageUtil.getBoardPosX(posX, NW_X, LEN_X), boardPosY = ImageUtil.getBoardPosY(posY, NW_Y, LEN_Y);
        if (boardPosX == -1 || boardPosY == -1) {
            pieceWaitDisplay.setVisible(false);
            return;
        }

        if (!goGame.isEmpty(boardPosX, boardPosY)) {
            pieceWaitDisplay.setVisible(false);
            return;
        }

        pieceWaitDisplay.setVisible(true);

        pieceWaitDisplay.setX(ImageUtil.getAbsolutePosX(boardPosX, NW_X, LEN_X) - RADIUS + 1);
        pieceWaitDisplay.setY(ImageUtil.getAbsolutePosY(boardPosY, NW_Y, LEN_Y) - RADIUS + 1);
    }

    public void refreshSound() {
        sound.resetAll();
    }

    public void clean() {
        sound.recycle();
        loadSound();
        isRunning = false;
    }

    public void processResponse(String response) {
        GoLogger.debug("Client: Board received: " + response);
        if (response == null) return;
        if (!requestID.isEmpty()) requestID.remove();


        char opt = response.charAt(0);
        char status = response.charAt(1);
        String data = response.substring(2);

        switch (opt) {
            case 'h' -> checkHashCode(data);
            case 'p' -> putPieceFromResponse(status, data);
            case 's' -> skipTurn(status);
            case 'l' -> {}
            case 'r' -> recoverFromResponse(status, data);
        }
    }

    private void checkHashCode(String data) {
        int hashCodeServer = Integer.parseInt(data);
        int hashCodeClient = goGame.hashCode();
        if (hashCodeServer != hashCodeClient) {
            GoLogger.error("HashCode not match: " + hashCodeServer + " - " + hashCodeClient);
        }
    }

    public void putPieceFromResponse(char status, String data) {
        if (status == 'F') return;
        String[] pos = data.split(",");
        int x = Integer.parseInt(pos[0]), y = Integer.parseInt(pos[1]);
        setPieceDisplay(x, y);
    }

    public void recoverFromResponse(char status, String data) {
        if (status == 'F') return;
        GoLogger.debug("Client: Recover from server");
        recoverPieces(data);
    }

    public boolean checkFirstTime() {
        return requestTimes == 0;
    }

    public void setReadOnly() {
        isReadOnly = true;
    }

    @Override
    public String toString() {
        return goGame.toString();
    }

    @Override
    public void run() {
    }
}