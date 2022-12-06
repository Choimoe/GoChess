package GoGame;

import java.io.Serializable;

public class GoStep implements Serializable {
    private int x, y, player;

    public int getX()       { return x; }
    public int getY()       { return y; }
    public int getPlayer()  { return player; }

    public GoStep() {}

    public GoStep(int x, int y, int player) {
        this.x      = x;
        this.y      = y;
        this.player = player;
    }

    public GoStep(GoStep o) {
        this.x      = o.getX();
        this.y      = o.getY();
        this.player = o.getPlayer();
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", (" + player + ")]";
    }
}
