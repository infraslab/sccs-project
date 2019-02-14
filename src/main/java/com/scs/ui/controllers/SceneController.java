package com.scs.ui.controllers;

import com.scs.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;

import java.io.IOException;

import static com.scs.Main.debugText;

public class SceneController {
    private TextArea debugArea;

    public void goStage(String fxmlURL) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlURL));
        try {
            Parent root = loader.load();
            Main.mainStage.setScene(new Scene(root, 1024, 768));
            Main.mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDebugArea(TextArea debugArea) {
        this.debugArea = debugArea;
    }

    public void addDebug(String text) {
        debugText.append(text + "\n");
        debugArea.setText(String.valueOf(debugText));
        debugArea.setScrollTop(Double.MAX_VALUE);
    }
}
