package GoScene;

import javafx.scene.control.Button;

public class CustomButton {
    public Button button;
    public String name;

    int width  = 370;
    int height = 80;

    Button getButton() {
        return button;
    }

    void setSize(int width, int height) {
        if (width  >= 0) this.width  = width;
        if (height >= 0) this.height = height;
    }

    /**
     * CustomButton: Initialize the button with
     *  - width: this.width
     *  - height: this.height
     *  - name: Name
     *  - style: background color, text color and size
     *  TODO:
     *  - addEventHandler: add the animation of MOUSE_ENTERED and MOUSE_EXITED
     *    button.addEventHandler(MouseEvent.MOUSE_ENTERED, (e)->{});
     *    button.addEventHandler(MouseEvent.MOUSE_EXITED,  (e)->{});
     *
     * @param buttonName: the name which displayed on the button
     * @param size: should be zero or two integers, representing the width and height
     */
    public CustomButton(String buttonName, int ...size) {
        button = new Button(buttonName);
        name = buttonName;

        if (size.length != 0) setSize(size[0], size[1]);
        button.setPrefWidth(width); button.setPrefHeight(height);

        button.setStyle(
            "-fx-background-color:#E8E8E8;"+
            "-fx-background-radius:20;"+
            "-fx-text-fill:#4A88C7;"+
            "-fx-font: 24 YaHei"
        );
    }
}
