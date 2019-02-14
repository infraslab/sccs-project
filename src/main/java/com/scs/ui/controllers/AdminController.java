package com.scs.ui.controllers;

import com.scs.Main;
import com.scs.db.oracle.DBConnector;
import com.scs.input.ReadData;
import com.scs.utils.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.LinkedList;

import static com.scs.Main.debugText;
import static com.scs.variables.ControlVariables.dataGenerate;
import static com.scs.variables.ControlVariables.isInput;

public class AdminController {
    @FXML
    private Button logoutButton;
    @FXML
    private Label infoLabel;
    @FXML
    private TextArea debugArea;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label postLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Button changeProfileButton;
    @FXML
    private Button checkInputButton;
    @FXML
    private Button goGraphButton;
    @FXML
    private Label inputLabel;
    @FXML
    private Button confirmNoButton;
    @FXML
    private Button confirmYesButton;

    private SceneController sceneController;

    private ReadData readData;

    private LinkedList<String> userInfoList;

    @FXML
    private void initialize() {
        sceneController = new SceneController();
        readData = new ReadData();
        sceneController.setDebugArea(debugArea);
        sceneController.addDebug("Admin Page");
        debugArea.setText(String.valueOf(debugText));
        debugArea.setVisible(false);
        inputLabel.setVisible(false);
        infoLabel.setText("");
        confirmNoButton.setVisible(false);
        confirmYesButton.setVisible(false);
        goGraphButton.setVisible(false);
        userInfoList = DBConnector.getInstance().getUserInfo();
        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        usernameLabel.setText(userInfoList.get(0));
        phoneLabel.setText(userInfoList.get(1));
        emailLabel.setText(userInfoList.get(2));
        addressLabel.setText(userInfoList.get(3));
        postLabel.setText(userInfoList.get(4));
        countryLabel.setText(userInfoList.get(5));
    }

    @FXML
    private void confirmYesAction(ActionEvent actionEvent) {
        inputLabel.setVisible(true);
        inputLabel.setText("Then there might be some problems with the application, \nan email notification is sent to service provider, please \nwait until service provider contact you to fix this problem.");
        EmailSender emailSender = new EmailSender();
        String body = "username: " + usernameLabel.getText() + "<br>\n" +
                "email: " + emailLabel.getText() + "<br>\n" +
                "I have problem with input data, please check immediately!"
                ;
        emailSender.createEmailMessage("","Input Problem", body);
        emailSender.sendEmail();
        infoLabel.setText("An alert about no input is sent to service provider");
    }

    @FXML
    private void confirmNoAction(ActionEvent actionEvent) {
        inputLabel.setVisible(true);
        inputLabel.setText("Then please make sure that you have data and \nclick on Check Input Data button again");
        infoLabel.setText("");
    }

    @FXML
    private void goGraphAction(ActionEvent actionEvent) {
//        sceneController.goStage("/graphPage.fxml");
        GraphController graphController = new GraphController();
        dataGenerate = true;
        try {
            graphController.start(Main.mainStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void checkInputAction() {
        isInput = readData.checkInput();
        infoLabel.setText("");
//        isInput = false;
        if (isInput) {
            inputLabel.setVisible(true);
            inputLabel.setText("Input data exists! You can go to Visualisation Page!");
            goGraphButton.setVisible(true);
            confirmYesButton.setVisible(false);
            confirmNoButton.setVisible(false);
        } else {
            inputLabel.setVisible(true);
            inputLabel.setText("No input data! Are you sure that input exists?");
            goGraphButton.setVisible(false);
            confirmYesButton.setVisible(true);
            confirmNoButton.setVisible(true);
        }
    }

    @FXML
    private void changeProfileAction(ActionEvent actionEvent) {
        sceneController.goStage("/changeProfilePage.fxml");
    }

    @FXML
    private void logoutAction(ActionEvent actionEvent) {
        DBConnector.initialize();
        sceneController.goStage("/authPage.fxml");
    }
}
