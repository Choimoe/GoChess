package GoScene.WindowUtil;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class TestUtil extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Simple interface
        VBox root = new VBox(5);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        // Create a button to launch the input window
        Button button = new Button("Get input");
        button.setOnAction(e -> {

            // Create the new dialog
            TextAreaInputDialog dialog = new TextAreaInputDialog();
            dialog.setHeaderText(null);
            dialog.setGraphic(null);

            // Show the dialog and capture the result.
            Optional result = dialog.showAndWait();

            // If the"Okay" button was clicked, the result will contain our String in the get() method
            if (result.isPresent()) {
                System.out.println(result.get());
            }

        });

        root.getChildren().add(button);

        // Show the Stage
        primaryStage.setWidth(300);
        primaryStage.setHeight(300);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
