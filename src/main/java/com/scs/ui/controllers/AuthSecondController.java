package com.scs.ui.controllers;

import com.scs.auth.TokenGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AuthSecondController {
    @FXML
    private Button resendButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label infoLabel;
    @FXML
    private TextField tokenField;

    private SceneController sceneController;

    private String token;

    @FXML
    private void initialize() {
        sceneController = new SceneController();
        infoLabel.setText("The token is sent to your email, please check your email! \nPlease note that you only have 10 minutes to enter token, \nafter 10 minutes, you have to press Resend Token button to get new token!");
        generateToken();
    }

    @FXML
    private void confirmAction(ActionEvent actionEvent) {
        if (token.length() < 2) {
            infoLabel.setText("Your token is expired, please click Resend Token!");
        } else {
            if (tokenField.getText().equalsIgnoreCase(token)) {
                token = "";
                sceneController.goStage("/adminPage.fxml");
            } else {
                infoLabel.setText("Your token is not correct, please enter token again!");
            }
        }
    }

    @FXML
    private void cancelAction(ActionEvent actionEvent) {
        sceneController.goStage("/authPage.fxml");
    }

    @FXML
    private void resendAction(ActionEvent actionEvent) {
        generateToken();
        infoLabel.setText("The token is sent to your email, please check your email! \nPlease note that you only have 10 minutes to enter token, \nafter 10 minutes, you have to press Resend Token button to get new token!");
    }

    private void generateToken() {
        TokenGenerator generator = new TokenGenerator();
        token = generator.getToken();
        Thread tokenExpire = new Thread() {
            public void run() {
                try {
                    Thread.sleep(600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                token = "";
            }
        };
        tokenExpire.start();
    }
}
