package GoScene;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StartGamePage extends ButtonPages {
    /**
     * Set the StartGamePage
     */
    public StartGamePage() {
        initialButton(new String[]{"新一局", "回放", "返回"});

        VBox vBox = new VBox(new Label("This is HomePage"));
        Group group = new Group();
        scene = new Scene(group);

        VBox vbox = new VBox();
        vbox.setLayoutX(950);
        vbox.setLayoutY(350);

        vbox.setSpacing(20);
        for (int i = 0; i < buttonNumber; i++)
            vbox.getChildren().add(button[i].getButton());

        group.getChildren().add(vbox);
    }
}
