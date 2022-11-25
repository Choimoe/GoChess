package GoScene;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ButtonPages {
    int buttonNumber;
    public CustomButton[] button;
    String[] buttonName;
    protected Scene scene;

    private double oldStageX, oldStageY, oldScreenX, oldScreenY;

    public void setDragged(Stage stage) {
        scene.setOnMousePressed(event -> {
            oldStageX = stage.getX();
            oldStageY = stage.getY();
            oldScreenX = event.getScreenX();
            oldScreenY = event.getScreenY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - oldScreenX + oldStageX);
            stage.setY(event.getScreenY() - oldScreenY + oldStageY);
        });
    }

    public Scene getScene() { return scene; }

    void initialButton (String[] name) {
        buttonNumber = name.length;
        button = new CustomButton[buttonNumber];
        buttonName = name;

        for (int i = 0; i < buttonNumber; i++)
            button[i] = new CustomButton(buttonName[i]);
    }

    void initialButton (int width, int height, String[] name) {
        buttonNumber = name.length;
        button = new CustomButton[buttonNumber];
        buttonName = name;

        for (int i = 0; i < buttonNumber; i++)
            button[i] = new CustomButton(width, height, buttonName[i]);
    }

    public void setButtonJump(Stage stage, int index, Scene nextScene) {
        button[index].button.setOnAction((ActionEvent e) -> stage.setScene(nextScene));
    }

    public void setButtonAction(int index, ButtonLambda buttonAction) {
        button[index].button.setOnAction((ActionEvent e) -> buttonAction.buttonAction());
    }

    public void setButtonJump(Stage stage, int index, Scene nextScene, ButtonLambda buttonAction) {
        button[index].button.setOnAction((ActionEvent e) -> {
            stage.setScene(nextScene);
            buttonAction.buttonAction();
        });
    }
}
