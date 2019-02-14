package com.scs;

import com.scs.db.oracle.DBConnector;
import com.scs.utils.EmailSender;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    public static Stage mainStage;

    public static volatile boolean firstTimeOpen = true;

    public static StringBuilder debugText = new StringBuilder();

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/authPage.fxml"));
        try {
            Parent root = loader.load();
            primaryStage.setTitle("Human Activity Recognition");
            primaryStage.setScene(new Scene(root, 1024, 768));
            primaryStage.setMinHeight(768);
            primaryStage.setMinWidth(1024);
            primaryStage.setMaxHeight(768);
            primaryStage.setMaxWidth(1024);
            primaryStage.show();

            primaryStage.setOnCloseRequest(t -> {
                DBConnector.reset();
                Platform.exit();
                System.exit(0);
            });
            mainStage = primaryStage;
            DBConnector.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
