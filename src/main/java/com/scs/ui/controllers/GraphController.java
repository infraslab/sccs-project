package com.scs.ui.controllers;

import com.scs.algorithms.AlgorithmRunner;
import com.scs.input.ReadData;
import com.scs.ui.graphs.RTGraph;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;

import static com.scs.variables.DataVariables.*;
import static com.scs.variables.ControlVariables.*;

public class GraphController extends Application {

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
    public void start(Stage primaryStage) {
        SceneController sceneController = new SceneController();

        Button backToMainPage = new Button("Back to Admin Page");
        backToMainPage.setMinSize(80, 40);

        backToMainPage.setOnAction(e -> {
            dataGenerate = false;
            startAlgo = false;
            sceneController.goStage("/adminPage.fxml");
        });

        LineChart lineChart1 = new GenerateGraph().createGraph("Acc X", 300, 300, 10, 50);
        LineChart lineChart2 = new GenerateGraph().createGraph("Acc Y", 300, 300, 350, 50);
        LineChart lineChart3 = new GenerateGraph().createGraph("Acc Z", 300, 300, 700, 50);
        LineChart lineChart4 = new GenerateGraph().createGraph("Total Acc", 400, 1000, 10, 350);

        Pane root = new Pane();

        root.getChildren().add(backToMainPage);
        root.getChildren().add(lineChart1);
        root.getChildren().add(lineChart2);
        root.getChildren().add(lineChart3);
        root.getChildren().add(lineChart4);

        Scene scene = new Scene(root, 1024, 768);

        primaryStage.setScene(scene);
        primaryStage.show();
        startAlgo = true;

        ReadData readData = new ReadData();
        readData.start();
        AlgorithmRunner algorithmRunner = new AlgorithmRunner(5,0.5);
        algorithmRunner.start();

    }

    private class GenerateGraph {
        public LineChart createGraph(String title, double height, double width, double layoutX, double layoutY) {
            RTGraph rtGraph = new RTGraph(title, -1, 10);
            LineChart lineChart = rtGraph.getLineChart();
            rtGraph.executeGraph();
            lineChart.setTitle(title);
            lineChart.setPrefHeight(height);
            lineChart.setPrefWidth(width);
            lineChart.setLayoutX(layoutX);
            lineChart.setLayoutY(layoutY);
            return lineChart;

        }
    }
}

