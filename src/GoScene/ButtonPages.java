package GoScene;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Objects;

public class ButtonPages {
    int                     buttonNumber;
    String[]                buttonName;
    protected Scene         scene;
    public CustomButton[]   button;

    private double oldStageX, oldStageY, oldScreenX, oldScreenY;

    public Scene getScene() {
        return scene;
    }

    /**
     * setDragged: let the stage can be dragged on *this* scene
     * @param stage: the parent stage
     */
    public void setDragged(Stage stage) {
        scene.setOnMousePressed(event -> {
            oldStageX   = stage.getX();
            oldStageY   = stage.getY();
            oldScreenX  = event.getScreenX();
            oldScreenY  = event.getScreenY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - oldScreenX + oldStageX);
            stage.setY(event.getScreenY() - oldScreenY + oldStageY);
        });
    }

    /**
     * initialButton: set the button with given name and size
     * @param name: the names which displayed on the buttons
     * @param size: should be zero or two integers, representing the width and height
     */
    protected void initialButton(String[] name, int... size) {
        buttonNumber = name.length;
        button = new CustomButton[buttonNumber];
        buttonName = name;

        for (int i = 0; i < buttonNumber; i++)
            button[i] = new CustomButton(buttonName[i], size);
    }

    /**
     * setButtonJump: set the jump action
     * @param stage: the parent stage
     * @param index: select the button on the scene
     * @param nextScene: the scene to jump when click the button
     */
    public void setButtonJump(Stage stage, int index, Scene nextScene) {
        button[index].button.setOnAction((ActionEvent e) -> stage.setScene(nextScene));
    }

    /**
     * setButtonAction: set the action of the button
     * @param index: select the button on the scene
     * @param buttonAction: do the action when click the button
     */
    public void setButtonAction(int index, ButtonLambda buttonAction) {
        button[index].button.setOnAction((ActionEvent e) -> buttonAction.buttonAction());
    }

    /**
     * setButtonAction: set the action of the button
     * @param temp: the button on the scene
     * @param buttonAction: do the action when click the button
     */
    public void setButtonAction(Button temp, ButtonLambda buttonAction) {
        temp.setOnAction((ActionEvent e) -> buttonAction.buttonAction());
    }

    /**
     * setButtonAction: set the action of the button
     * @param buttonName: the name of the button
     * @param buttonAction: do the action when click the button
     */
    public void setButtonAction(String buttonName, ButtonLambda buttonAction) {
        for (CustomButton but : button) {
            if (Objects.equals(but.name, buttonName)) but.button.setOnAction((ActionEvent e) -> buttonAction.buttonAction());
        }
//        button[index].button.setOnAction((ActionEvent e) -> buttonAction.buttonAction());
    }

    /**
     * setButtonJump: set the jump action and do action of the button
     * @param stage: the parent stage
     * @param index: select the button on the scene
     * @param nextScene: the scene to jump when click the button
     * @param buttonAction: do the action **after** the jump
     */
    public void setButtonJump(Stage stage, int index, Scene nextScene, ButtonLambda buttonAction) {
        button[index].button.setOnAction((ActionEvent e) -> {
            stage.setScene(nextScene);
            buttonAction.buttonAction();
        });
    }

    public void clear() {}
    public void cleanPages() {}
    public void createClient() {}
}
