package com.scs.ui.controllers;

import com.scs.utils.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ForgotController {
    @FXML
    private TextArea debugArea;
    @FXML
    private Label infoLabel;
    @FXML
    private Button submitButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField mailField;
    @FXML
    private TextField ques1Field;
    @FXML
    private TextField ques2Field;
    @FXML
    private TextField ques3Field;

    private SceneController sceneController;

    @FXML
    private void initialize() {
        infoLabel.setText("");
        sceneController = new SceneController();
        sceneController.setDebugArea(debugArea);
        sceneController.addDebug("Forget scene opened");
    }

    @FXML
    private void submitAction(ActionEvent actionEvent) {
        EmailSender emailSender = new EmailSender();

        String body = "username: " + usernameField.getText() + "<br>\n" +
                "email: " + mailField.getText() + "<br>\n" +
                "Answer 1: " + ques1Field.getText() + "<br>\n" +
                "Answer 2: " + ques2Field.getText() + "<br>\n" +
                "Answer 3: " + ques3Field.getText() + "<br>\n"
        ;
        emailSender.createEmailMessage("","Password Reset", body);
        emailSender.sendEmail();
        sceneController.goStage("/authPage.fxml");
    }
}
