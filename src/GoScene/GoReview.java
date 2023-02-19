package GoScene;

import GoBoard.ChessBoard;

public class GoReview extends ButtonPages {
    BoardPage source;

    public GoReview(BoardPage source) {
        this.source = source;
        buttonNumber = source.buttonNumber;
        buttonName = source.buttonName;
        scene = source.scene;
        button = source.button;
    }

    protected void initialButton(String[] name, int... size) {
        buttonNumber = name.length;
        button = new CustomButton[buttonNumber];
        buttonName = name;

        for (int i = 0; i < buttonNumber; i++)
            button[i] = new CustomButton(buttonName[i], size);
    }

    public ChessBoard getBoard() {
        return source.getChessBoard();
    }
}
