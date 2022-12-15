package GoScene;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class HomePage extends ButtonPages {
    /**
     * Set the HomePage
     */
    public HomePage() {
        initialButton(new String[]{"开始", "设置", "退出"});

        Group group = new Group();
        scene       = new Scene(group);

        /* set the position of the display box */
        VBox vbox   = new VBox();

        vbox.setLayoutX(950);
        vbox.setLayoutY(350);

        /* set the space between two object */
        vbox.setSpacing(20);

        /* add the button to display box */
        for (int i = 0; i < buttonNumber; i++)
            vbox.getChildren().add(button[i].getButton());

        /* put the display box into the group */
        group.getChildren().add(vbox);
    }
}
