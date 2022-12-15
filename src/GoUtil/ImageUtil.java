package GoUtil;

import javafx.scene.image.ImageView;

public class ImageUtil {
    public static double abs(double x) { return x < 0 ? -x : x; }

    public static int getBoardPosX (double posX, double NW_X, double LEN_X) {
        double divided = (posX - NW_X) / LEN_X;
        int getInt = (int)(divided + 0.5);
        if (abs(divided - getInt) > 0.3 || getInt >= 19) return -1;
        else return getInt;
    }

    public static int getBoardPosY (double posY, double NW_Y, double LEN_Y) {
        double divided = (posY - NW_Y) / LEN_Y;
        int getInt = (int)(divided + 0.5);
        if (abs(divided - getInt) > 0.3 || getInt >= 19) return -1;
        else return getInt;
    }


    public static int getAbsolutePosX (int posX, double NW_X, double LEN_X) { return (int)(posX * LEN_X + NW_X); }
    public static int getAbsolutePosY (int posY, double NW_Y, double LEN_Y) { return (int)(posY * LEN_Y + NW_Y); }

    /**
     * reshapeImageWithHeight: change the height of the image by *keeping ratio*
     * @param imageView: the image waiting change
     * @param height: the height of the image
     */
    public static void reshapeImageWithHeight(ImageView imageView, int height) {
        if (imageView == null) return;
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(height);
    }
}
