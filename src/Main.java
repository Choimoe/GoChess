/**
 * repo: https://github.com/Choimoe/GoChess
 */

import GoScene.MainControl;

public class Main {
    public static void main(String[] args) {
        MainControl GUI = new MainControl();
        GUI.display();
        GUI.clean();
        System.out.println("[DEBUG] Main Function Ended.");
    }
}