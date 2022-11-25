package GoScene;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class CustomButton {
    public Button button;
    String name;
    int width = 370;
    int height = 80;

    void setSize(int width, int height) {
        if (width >= 0) this.width = width;
        if (height >= 0) this.height = height;
    }

    public CustomButton(String Name) {
        button = new Button(Name);
        name = Name;

        button.setPrefWidth(width); button.setPrefHeight(height);

        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (e)->{
//            System.out.println("Button \"" + name + "\" mouse entered");
        });

        button.addEventHandler(MouseEvent.MOUSE_EXITED, (e)-> {
//            System.out.println("Button \"" + name + "\" mouse out");
        });

        button.setStyle(
            "-fx-background-color:#E8E8E8;"+ //设置背景颜色
            "-fx-background-radius:20;"+     //设置背景圆角
            "-fx-text-fill:#4A88C7;"+        //设置字体颜色
            "-fx-font: 24 YaHei"
        );
    }

    public CustomButton(int width, int height, String Name) {
        button = new Button(Name);
        name = Name;

        button.setPrefWidth(width); button.setPrefHeight(height);

        button.setStyle(
                "-fx-background-color:#E8E8E8;"+ //设置背景颜色
                "-fx-background-radius:20;"+     //设置背景圆角
                "-fx-text-fill:#4A88C7;"+        //设置字体颜色
                "-fx-font: 24 YaHei"
        );
    }

    Button getButton() {
        return button;
    }
}
