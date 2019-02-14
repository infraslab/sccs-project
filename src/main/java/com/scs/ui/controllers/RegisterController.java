package com.scs.ui.controllers;

import com.scs.db.oracle.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.scs.Main.debugText;

public class RegisterController {

    @FXML
    private Label infoLabel;
    @FXML
    private TextField ques1Field;
    @FXML
    private TextField ques2Field;
    @FXML
    private TextField ques3Field;

    @FXML
    private TextArea debugArea;
    @FXML
    private Label passwordPolicy;
    @FXML
    private Button registerButton;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
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

    private ArrayList<TextField> infoList;

    private DBConnector connector;

    @FXML
    private void initialize() {
        connector = DBConnector.getInstance();
        infoList = new ArrayList<>();
        infoList.add(usernameField);
        infoList.add(phoneField);
        infoList.add(mailField);
        infoList.add(addressField);
        infoList.add(postField);
        infoList.add(countryField);
        infoList.add(ques1Field);
        infoList.add(ques2Field);
        infoList.add(ques3Field);
        sceneController = new SceneController();
        sceneController.setDebugArea(debugArea);
        sceneController.addDebug("Register page opened");
        debugArea.setText(String.valueOf(debugText));
        debugArea.setVisible(false);
        infoLabel.setText("");
        passwordPolicy.setText("The user name of each user is unique, and the password must follow following rules:\n" +
                "\t– Be a minimum of eight (8) characters in length\n" +
                "\t– Contain at least one (1) character from three (3) of the following categories:\n" +
                "\t\tUppercase letter (A-Z), Lowercase letter (a-z), Digit (0-9) and Special character (#$@%)");
    }

    @FXML
    private void registerAction() {
        boolean registerVerified = true;
        boolean userExisting;
        String pass = passwordField.getText();
        String cfPass = cfPasswordField.getText();
//        System.out.println(pass + "\t" + cfPass);
        for (TextField textField : infoList) {
            if (textField.getText().length() <= 0) {
                registerVerified = false;
                infoLabel.setText("Please fill all the fields!");
//                System.out.println(textField.getText());
            }
        }
        if (!pass.equals(cfPass)) {
            infoLabel.setText("Please make sure the confirm password matches with password!");
            registerVerified = false;
        }

        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pass);
        boolean b = m.find();
        if (pass.length() < 8 || !b) {
            infoLabel.setText("Password does not meet the policy, please check again.");
            registerVerified = false;
        }
//        System.out.println(registerVerified);
        if (registerVerified) {
            userExisting = DBConnector.getInstance().addUser(usernameField.getText(), pass, phoneField.getText(), mailField.getText(), addressField.getText(), postField.getText(), countryField.getText(), ques1Field.getText(), ques2Field.getText(), ques3Field.getText());
            if (!userExisting) {
                infoLabel.setText("This username or email is already existing, please try another one!");
                registerVerified = false;
            }
        }

        if (registerVerified) {
            sceneController.goStage("/authPage.fxml");
        }
    }

    @FXML
    private void checkConfirmPassword(KeyEvent keyEvent) {
//        System.out.println(cfPasswordField.getText());
    }

    @FXML
    private void cancelRegister(ActionEvent actionEvent) {
        sceneController.goStage("/authPage.fxml");
    }
}
