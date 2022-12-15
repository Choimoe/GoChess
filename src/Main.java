import GoScene.MainControl;
import GoServer.GoServerMain;

public class Main {
    public static void main(String[] args) {
        Thread server = new Thread(() -> {
            GoServerMain goServer = new GoServerMain();
            goServer.run();
        });
        server.start();

        MainControl GUI = new MainControl();
        GUI.display();

        server.interrupt();
        GUI.clean();

        System.exit(0);
    }
}