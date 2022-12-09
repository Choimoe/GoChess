package GoGame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoMain implements Serializable {
    final int SIZE = 19;
    final int WAIT_BEGIN = 0, BLACK_PLAYER = 1, WHITE_PLAYER = 2;

    protected int[][] goMap;
    protected List<GoStep> goSteps;
    private final GoStep deletedStep = new GoStep(-1, -1, -1);

    protected int steps, currentUser, gameEnded;

    public int getSteps()               { return steps;         }
    public int getCurrentPlayer()       { return currentUser;   }
    public List<GoStep> getGoSteps()    { return goSteps;       }
    public int getPosStep(int x, int y) { return goMap[x][y];   }

    /**
     * beginGame: start game, set the user to black player
     */
    public void beginGame() {
        currentUser = BLACK_PLAYER;
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
        if (!isLegal(boardX, boardY))                                       return false;
        if (!GoLiberty.checkPosition(goMap, boardX, boardY, steps + 1))     return false;

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
        gameEnded   = WAIT_BEGIN;
        steps       = 0;

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) goMap[i][j] = WAIT_BEGIN;

        goSteps.clear();
        for (int i = 0; i < SIZE * SIZE * 10; i++) goSteps.add(deletedStep);
    }

    /**
     * Initialize the objects
     */
    public GoMain() {
        goMap   = new int[SIZE][SIZE];
        goSteps = new ArrayList<>();

        for (int i = 0; i < SIZE * SIZE * 10; i++) goSteps.add(deletedStep);
        steps   = 0;

        clear();
    }

    /**
     * skip the turn
     */
    public void skipTurn() {
        steps++;
        changePlayer();
    }

    @SuppressWarnings("RegExpRedundantEscape")
    public void recover(String data) {
        clear();
        Pattern patCounter      = Pattern.compile("CT\\[(\\d+)\\]");
        Pattern patPlayer       = Pattern.compile("CU\\[(\\d+)\\]");
        Pattern patPiece        = Pattern.compile("(B|W)\\[([a-z]{2})\\]");

        Matcher matcherCounter  = patCounter.matcher(data);
        Matcher matcherPlayer   = patPlayer.matcher (data);
        Matcher matcherPiece    = patPiece.matcher  (data);

        if (matcherCounter.find()) steps        = Integer.parseInt(matcherCounter.group(1));
        if (matcherPlayer .find()) currentUser  = Integer.parseInt(matcherPlayer .group(1));

        System.out.println("[DEBUG] matcherCounter: " + matcherCounter.group() + " -> " + steps      );
        System.out.println("[DEBUG] matcherPlayer : " + matcherPlayer .group() + " -> " + currentUser);

        int curStep = 0;
        while (matcherPiece.find()) {
            String match        = matcherPiece.group();

            int x = match.charAt(2) - 'a';
            int y = match.charAt(3) - 'a';

            if (match.charAt(0) == 'B') {
                if ((curStep & 1) == 1) curStep++; goMap[x][y] = ++curStep;
                goSteps.set(curStep, new GoStep(x, y, BLACK_PLAYER));
            } else {
                if ((curStep & 1) == 0) curStep++; goMap[x][y] = ++curStep;
                goSteps.set(curStep, new GoStep(x, y, WHITE_PLAYER));
            }
        }

        if (curStep > steps) steps = 2 * curStep + currentUser;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("(;GM[1]FF[4]CA[UTF-8]SZ[19]KM[6.5]PB[Black]PW[White]");
        result.append("CT[").append(steps       ).append("]");
        result.append("CU[").append(currentUser ).append("]");

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (goMap[i][j] == WAIT_BEGIN) continue;
                String pos = (char)((int)'a' + i) + "" + (char)((int)'a' + j);
                if ((goMap[i][j] & 1) == 1) result.append('B');
                else result.append('W');
                result.append('[').append(pos).append(']');
            }
        }

        result.append(')');
        return result.toString();
    }
}
