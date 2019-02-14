package com.scs.ui.controllers;

import com.scs.db.oracle.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.LinkedList;

import static com.scs.Main.debugText;

public class ChangeProfileController {

    @FXML
    private Label infoLabel;

    @FXML
    private TextArea debugArea;
    @FXML
    private Label passwordPolicy;
    @FXML
    private Button confirmButton;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField cfPasswordField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField mailField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postField;
    @FXML
    private TextField countryField;

    private SceneController sceneController;

    private LinkedList<String> userInfoList;

    @FXML
    private void initialize() {
        sceneController = new SceneController();
        sceneController.setDebugArea(debugArea);
        sceneController.addDebug("User Change Page");
        debugArea.setText(String.valueOf(debugText));
        debugArea.setVisible(false);
        infoLabel.setText("");
        passwordPolicy.setVisible(true);
        passwordPolicy.setText("The user name of each user is unique, and the password must follow following rules:\n" +
                "\t– Be a minimum of eight (8) characters in length\n" +
                "\t– Contain at least one (1) character from three (3) of the following categories:\n" +
                "\t\tUppercase letter (A-Z), Lowercase letter (a-z), Digit (0-9) and Special character (#$@%)");
        userInfoList = DBConnector.getInstance().getUserInfo();
        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        phoneField.setText(userInfoList.get(1));
        mailField.setText(userInfoList.get(2));
        addressField.setText(userInfoList.get(3));
        postField.setText(userInfoList.get(4));
        countryField.setText(userInfoList.get(5));
    }

    @FXML
    private void confirmAction() {
        boolean changeSuccess = false;
        changeSuccess = DBConnector.getInstance().changeUserInfo(passwordField.getText(), phoneField.getText(), mailField.getText(), addressField.getText(), postField.getText(), countryField.getText());
        if (changeSuccess) {
            DBConnector.initialize();
            sceneController.goStage("/authPage.fxml");
        } else {
            infoLabel.setText("Please make sure the information is correct!");
        }

    }

    @FXML
    private void cancelAction(ActionEvent actionEvent) {
        sceneController.goStage("/adminPage.fxml");
    }
}
