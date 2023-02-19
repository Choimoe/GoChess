package GoScene;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class StartGamePage extends ButtonPages {
    /**
     * Set the StartGamePage
     */
    public StartGamePage() {
        initialButton(new String[]{"布棋", "对弈", "观悟", "返回"});

        Image image;
        try {
            image = new Image(new FileInputStream("assets/startPage.png"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ImageView imageView = new ImageView(image);

        Group group = new Group();
        scene       = new Scene(group);

        VBox vbox = new VBox();
        HBox hbox = new HBox();

        vbox.setLayoutX(950);
        vbox.setLayoutY(350);

        vbox.setSpacing(20);
        for (int i = 0; i < buttonNumber; i++)
            vbox.getChildren().add(button[i].getButton());

        hbox.getChildren().add(imageView);

        group.getChildren().add(hbox);
        group.getChildren().add(vbox);
    }
}
