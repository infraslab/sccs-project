package com.scs.ui.controllers;

import com.scs.db.oracle.DBConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import static com.scs.Main.debugText;


public class AuthController {

    @FXML
    private Label infoLabel;

    @FXML
    private Button forgotButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextArea debugArea;

    private SceneController sceneController;

    @FXML
    private void initialize() {
        infoLabel.setVisible(true);
        infoLabel.setText("");
        passwordField.clear();
        usernameField.clear();
        debugArea.setText(String.valueOf(debugText));
        debugArea.setVisible(false);
        sceneController = new SceneController();
        sceneController.setDebugArea(debugArea);
    }

    @FXML
    private void loginAction() {
        if (usernameField.getText().length() > 0 && passwordField.getText().length() > 0) {
            boolean verified = DBConnector.getInstance().verifyUser(usernameField.getText(), passwordField.getText());
            if (verified) {
                sceneController.addDebug("Login button pressed");
                sceneController.goStage("/authSecondPage.fxml");
//                sceneController.goStage("/adminPage.fxml");
            }
            else {
                infoLabel.setText("Invalid username or password!");
            }
        } else {
            infoLabel.setText("Invalid username or password!");
        }

    }

    @FXML
    private void keyEnterPressedAction(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            loginAction();
        }
    }

    @FXML
    private void registerAction() {
        sceneController.goStage("/registerPage.fxml");
    }

    @FXML
    private void forgotAction() {
        sceneController.goStage("/forgotPage.fxml");
    }
}
