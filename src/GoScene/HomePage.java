package GoScene;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HomePage extends ButtonPages {
    /**
     * Set the HomePage
     */
    public HomePage() {
        initialButton(new String[]{"开始", "设置", "退出"});

        Image image, title;
        try {
            image = new Image(new FileInputStream("assets/homePage.png"));
            title = new Image(new FileInputStream("assets/title.png"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ImageView imageView = new ImageView(image);
        ImageView titleView = new ImageView(title);

        Group group = new Group();
        scene       = new Scene(group);

        /* set the position of the display box */
        VBox vbox   = new VBox();
        VBox titBox = new VBox();
        HBox hbox   = new HBox();

        vbox.setLayoutX(950);
        vbox.setLayoutY(350);

        titBox.setLayoutX(904);

        /* set the space between two object */
        vbox.setSpacing(20);

        /* add the button to display box */
        for (int i = 0; i < buttonNumber; i++)
            vbox.getChildren().add(button[i].getButton());

        hbox    .getChildren().add(imageView);
        titBox  .getChildren().add(titleView);

        /* put the display box into the group */
        group.getChildren().add(hbox    );
        group.getChildren().add(titBox  );
        group.getChildren().add(vbox    );
    }
}
