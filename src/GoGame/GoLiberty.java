package GoGame;

import java.util.ArrayList;
import java.util.List;

public class GoLiberty {
    private final static int ROW = 19, COL = 19;
    private static final int[][] visited = new int[ROW][COL];
    private static int[][] goMap;
    private static final int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    private static int color = 0;

    /**
     * GoLiberty.isLegal: check whether the position in the range of the map
     * @param x,y: the position of the map
     * @return if the position is in the range of the map
     */
    private static boolean isLegal(int x, int y) {
        return ((x >= 0) && (x < ROW) && (y >= 0) && (y < COL));
    }

    /**
     * GoLiberty.DFS: search the whole block to check the "Chi"
     * TODO: Some Bugs in it(Stack Overflow)
     * @param x,y: position
     * @param col: the kind of the piece
     *           - odd : black piece
     *           - even: white piece
     * @return if the position is liberty(have "Chi")
     */
    private static boolean DFS(int x, int y, int col) {
        visited[x][y] = color;

        for (int dir = 0; dir < 4; dir++) {
            int nextX = x + directions[dir][0], nextY = y + directions[dir][1];
            if (!isLegal(nextX, nextY)) continue;
            if (goMap[nextX][nextY] == 0) return true;
            if (visited[nextX][nextY] != 0) continue;
            if ((goMap[nextX][nextY] & 1) != col) continue;
            if (DFS (nextX, nextY, col)) return true;
        }

        return false;
    }

    /**
     * GoLiberty.startDFS: start DFS on every position
     * @return if there exists a block that is not liberty(not have "Chi")
     */
    private static int startDFS() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (visited[i][j] == 0 && goMap[i][j] != 0) {
                    color++;
//                    System.out.println("now DFS: (" + i + "," + j + "): " + goMap[i][j]);
                    if (!DFS(i, j, goMap[i][j] & 1)) return color;
                }
            }
        }
        return -1;
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
        color = 0;

        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++) visited[i][j] = 0;

        goMap[posX][posY] = step;

        boolean result = DFS(posX, posY, step & 1);

        goMap[posX][posY] = 0;

        return result;
    }

    /**
     * GoLiberty.check: get the
     * @param goMapOri: the set the value of this.goMap
     * @return the list of pieces which we need to delete
     */
    public static List<GoStep> check(int[][] goMapOri) {
        List<GoStep> result = new ArrayList<>();
        goMap = goMapOri;
        color = 0;

        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++) visited[i][j] = 0;

        int removeColor = startDFS();
        if (removeColor == -1) return null;

//        for (int i = 0; i < ROW; i++) {
//            for (int j = 0; j < COL; j++) {
//                System.out.print(goMap[i][j] + "(" + visited[i][j] + ")  ");
//            }
//            System.out.println();
//        }

        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++)
                if (visited[i][j] == removeColor)
                    result.add(new GoStep(i, j, goMap[i][j] & 1));
        return result;
    }
}
