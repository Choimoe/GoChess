package GoGame;

import java.io.Serializable;
import java.util.List;

public class GoMain implements Serializable {
    final int SIZE = 19;
    final int WAIT_BEGIN = 0, BLACK_PLAYER = 1, WHITE_PLAYER = 2;
    protected int[][] goMap = new int[SIZE][SIZE];
    protected GoStep[] goSteps = new GoStep[SIZE * SIZE * 10];
    protected int steps = 0;
    protected int currentUser, gameEnded;

    public void clear() {
        currentUser = WAIT_BEGIN;
        gameEnded = WAIT_BEGIN;
        steps = 0;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) goMap[i][j] = WAIT_BEGIN;
    }

    public GoStep[] getGoSteps() {
        return goSteps;
    }

    public int getPosStep (int x, int y) {
        return goMap[x][y];
    }

    public void beginGame() {
        currentUser = BLACK_PLAYER;
    }

    public int getCurrentPlayer() {
        return currentUser;
    }

    public int getLastPlayer() {
        return BLACK_PLAYER + WHITE_PLAYER - currentUser;
    }

    public boolean isEmpty(int x, int y) {
        return goMap[x][y] == WAIT_BEGIN;
    }

    public boolean isLegal(int x, int y) {
        return gameEnded == 0 && isEmpty(x, y);
    }

    public void changePlayer() {
        currentUser = BLACK_PLAYER + WHITE_PLAYER - currentUser;
    }

    public List<GoStep> getRemovePieces() {
        return GoLiberty.check(goMap);
    }

    public void removePiece(List<GoStep> list) {
        if (list == null) return;
        list.forEach(step -> goMap[step.getX()][step.getY()] = WAIT_BEGIN);
    }

    public boolean putPiece(int x, int y) {
//        System.out.println("location: " + x + " " + y + " " + gameEnded + " " + goMap[x][y]);
        if (!isLegal(x, y)) return false;
        if (!GoLiberty.checkPosition(goMap, x, y, steps + 1)) return false;
//        System.out.println("Success: " + x + " " + y + " " + currentUser);
        goSteps[++steps] = new GoStep(x, y, currentUser);
        goMap[x][y] = steps;
        changePlayer();
        return true;
    }

    public GoMain() {
        currentUser = WAIT_BEGIN;
        gameEnded = WAIT_BEGIN;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) goMap[i][j] = WAIT_BEGIN;
    }
}
