package GoGame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GoLiberty {
    private final static int ROW = 19, COL = 19;
    private static final int[][] visited = new int[ROW][COL];
    private static int[][] goMap;
    private static final int[][] directions4 = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    /**
     * GoLiberty.isIllegal: check whether the position in the range of the map
     * @param x,y: the position of the map
     * @return if the position is out the range of the map
     */
    private static boolean isIllegal(int x, int y) {
        return ((x < 0) || (x >= ROW) || (y < 0) || (y >= COL));
    }

    /**
     * GoLiberty.libertySearch: search the whole block to check the "Chi"
     * @param x,y: position
     * @param player: the kind of the piece
     *           - odd : black piece
     *           - even: white piece
     * @param color: to let the value of visited be color
     * @return if the position is liberty(have "Chi")
     */
    private static boolean libertySearch(int x, int y, int player, int color) {
        Queue<GoStep> queue = new LinkedList<>();
        clearVisited();

        queue.add(new GoStep(x, y, player));

        while(!queue.isEmpty()) {
            GoStep step = queue.remove();

            if (visited[step.getX()][step.getY()] != 0) continue;
            visited[step.getX()][step.getY()] = color;

            for (int dir = 0; dir < 4; dir++) {
                int nextX = step.getX() + directions4[dir][0], nextY = step.getY() + directions4[dir][1];
                if (isIllegal(nextX, nextY)) continue;

                if (goMap[nextX][nextY] == 0) return true;
                if (visited[nextX][nextY] != 0) continue;
                if ((goMap[nextX][nextY] & 1) != step.getPlayer()) continue;

                queue.add(new GoStep(nextX, nextY, step.getPlayer()));
            }
        }

        return false;
    }

    /**
     * GoLiberty.startSearch: start BFS on given direction
     * @param x: the x of starting position
     * @param y: the y of starting position
     * @param dir: the direction of starting bfs
     * @return if there exists a block that is not liberty(not have "Chi")
     */
    private static List<GoStep> startSearch(int x, int y, int dir) {
        int color = dir + 1;
        int nextX = x + directions4[dir][0], nextY = y + directions4[dir][1];

        if (isIllegal(nextX, nextY))  return null;
        if (goMap[nextX][nextY] == 0) return null;

        if (libertySearch(nextX, nextY, goMap[nextX][nextY] & 1, color)) return null;

        /* do not eat itself */
        if (visited[x][y] == color) return null;

        List<GoStep> result = new ArrayList<>();

        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++)
                if (visited[i][j] == color) result.add(new GoStep(i, j, goMap[i][j] & 1));

        return result;
    }

    /**
     * GoLiberty.checkPosition: check the piece if it can alive after put it.
     * I put the piece on the map and do DFS, and after check, recover the piece.
     * @param goMapOri: the chess map
     * @param posX,posY: the position of the piece
     * @param step: the step number of the piece
     *            - odd : black
     *            - even: white
     * @return if the piece can safely be put on the map
     */
    public static boolean checkPosition(int[][] goMapOri, int posX, int posY, int step) {
        goMap = goMapOri;

        goMap[posX][posY] = step;

        boolean result = libertySearch(posX, posY, step & 1, 1);

        if (result) {
            goMap[posX][posY] = 0;
            return true;
        }

        for (int dir = 0; dir < 4; dir++) {
            int nextX = posX + directions4[dir][0], nextY = posY + directions4[dir][1];
            if (isIllegal(nextX, nextY)) continue;
            if ((goMap[nextX][nextY] & 1) == (step & 1)) continue;
            if (!libertySearch(nextX, nextY, goMap[nextX][nextY] & 1, dir + 2)) {
                goMap[posX][posY] = 0;
                return true;
            }
        }

        goMap[posX][posY] = 0;
        return false;
    }

    /**
     * GoLiberty.check: get the deleted pieces list
     * @param goMapOri: the set the value of this.goMap
     * @return [List<GoStep>] the list of pieces which we need to delete
     */
    public static List<GoStep> check(int[][] goMapOri, int boardPosX, int boardPosY) {
        List<GoStep> result = new ArrayList<>();
        goMap = goMapOri;

        for (int dir = 0; dir < 4; dir++) {
            List<GoStep> temp = startSearch(boardPosX, boardPosY, dir);
            if (temp == null) continue;
            result.addAll(temp);
        }

        return result;
    }

    private static void clearVisited() {
        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++) visited[i][j] = 0;
    }
}
