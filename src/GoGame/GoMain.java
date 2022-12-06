package GoGame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GoMain implements Serializable {
    final int SIZE = 19;
    final int WAIT_BEGIN = 0, BLACK_PLAYER = 1, WHITE_PLAYER = 2;

    protected int[][] goMap;
    protected List<GoStep> goSteps;
    private final GoStep deletedStep = new GoStep(-1, -1, -1);

    protected int steps, currentUser, gameEnded;

    public List<GoStep> getGoSteps() {
        return goSteps;
    }

    public int getPosStep (int x, int y) {
        return goMap[x][y];
    }

    public int getSteps() {
        return steps;
    }

    /**
     * beginGame: start game, set the user to black player
     */
    public void beginGame() {
        currentUser = BLACK_PLAYER;
    }

    public int getCurrentPlayer() {
        return currentUser;
    }
    public int getLastPlayer() {
        return BLACK_PLAYER + WHITE_PLAYER - currentUser;
    }

    /**
     * isEmpty: check if the place is waiting a piece
     * @param x: the x of board position
     * @param y: the y of board position
     * @return if no piece on this place
     */
    public boolean isEmpty(int x, int y) {
        return goMap[x][y] == WAIT_BEGIN;
    }

    /**
     * isLegal: check that the position is empty and the game is not ended
     * @param x: the x of board position
     * @param y: the y of board position
     * @return if the piece is legal
     */
    public boolean isLegal(int x, int y) {
        return gameEnded == 0 && isEmpty(x, y);
    }

    /**
     * changePlayer: reverse the current user
     * currentUser must belong to {BLACK_PLAYER, WHITE_PLAYER}
     */
    public void changePlayer() {
        currentUser = BLACK_PLAYER + WHITE_PLAYER - currentUser;
    }

    /**
     * use GoLiberty.check() to get the piece updated
     * @return the list of pieces waiting to be deleted
     */
    public List<GoStep> getRemovePieces(int boardPosX, int boardPosY) {
        return GoLiberty.check(goMap, boardPosX, boardPosY);
    }

    /**
     * removePiece: delete the piece in the list
     * @param list: the list of pieces waiting to be deleted
     */
    public void removePiece(List<GoStep> list) {
        if (list == null) return;
        list.forEach(step -> goSteps.set(getPosStep(step.getX(), step.getY()), new GoStep(deletedStep)));
        list.forEach(step -> goMap[step.getX()][step.getY()] = WAIT_BEGIN);
    }

    /**
     * putPiece: try to put the piece
     * @param boardX: the x of board position
     * @param boardY: the y of board position
     * @return if successfully put the piece
     */
    public boolean putPiece(int boardX, int boardY) {
        if (!isLegal(boardX, boardY)) return false;
        if (!GoLiberty.checkPosition(goMap, boardX, boardY, steps + 1)) return false;

        steps += 1;
        goSteps.set(steps, new GoStep(boardX, boardY, currentUser));
        goMap[boardX][boardY] = steps;

        changePlayer();
        return true;
    }

    /**
     * clear the chess game
     *  - set the user to WAIT_BEGIN
     *  - set the goMap to WAIT_BEGIN
     */
    public void clear() {
        currentUser = WAIT_BEGIN;
        gameEnded = WAIT_BEGIN;
        steps = 0;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) goMap[i][j] = WAIT_BEGIN;
    }

    /**
     * Initialize the objects
     */
    public GoMain() {
        goMap = new int[SIZE][SIZE];
        goSteps = new ArrayList<>();
        for (int i = 0; i < SIZE * SIZE * 10; i++) goSteps.add(deletedStep);
        steps = 0;

        clear();
    }

    public void skipTurn() {
        steps++;
        changePlayer();
    }
}
