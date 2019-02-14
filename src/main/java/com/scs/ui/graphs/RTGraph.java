package com.scs.ui.graphs;

import javafx.animation.AnimationTimer;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.scs.variables.DataVariables.*;

public class RTGraph {

    private static final int MAX_DATA_POINTS = 100;

    private XYChart.Series series;
    private int xSeriesData = 0;
    private ConcurrentLinkedQueue<Number> dataQ = new ConcurrentLinkedQueue<>();
    private ExecutorService executor;
    private NumberAxis xAxis;
    private NumberAxis yAxis;

    private LineChart<Number, Number> lineChart;


    private AddToQueue addToQueue;

    private String graphName;

    public RTGraph(String name, double lowRange, double upperRange) {
        graphName = name;
        init(lowRange, upperRange, 0.5);
        yAxis.setLabel("Accelerometer in G");
    }

    /**
     * initialize chart
     * @param lowerRange set the lowest y value for the chart
     * @param upperRange set the highest y value for the chart
     * @param tickUnit how far y values are seperated
     */
    private void init(double lowerRange, double upperRange, double tickUnit) {
        xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);

        yAxis = new NumberAxis(lowerRange, upperRange, tickUnit);
        //yAxis.setAutoRanging(true);

        //-- Chart
        lineChart = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        lineChart.setAnimated(false);

        xAxis.setLabel("Number of Samples");

        //-- Chart Series
        series = new XYChart.Series<Number, Number>();
        lineChart.getData().add(series);
    }

    /**
     *
     * @return LineChart
     */
    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }

    /**
     * Timeline gets called in the JavaFX thread
     */
    private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }


    /**
     * update graph with new x and y values
     */
    private void addDataToSeries() {
        for (int i = 0; i < MAX_DATA_POINTS; i++) { //-- add 20 numbers to the plot+
            if (dataQ.isEmpty()) break;
            if (xSeriesData > 1000) xSeriesData = 0;
            series.getData().add(new LineChart.Data(xSeriesData++, dataQ.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }
        // update
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);
    }


    private class AddToQueue implements Runnable {
        public void run() {
            // add a item of random data to queue
            double data = 0;
            try {
            if (graphName.contains("x") || graphName.contains("X")) {
                data = acceXGraph.take();
            } else if (graphName.contains("y") || graphName.contains("Y")) {
                data = acceYGraph.take();
            } else if (graphName.contains("z") || graphName.contains("Z")) {
                data = acceZGraph.take();
            } else if (graphName.contains("total") || graphName.contains("Total")) {
                data = totalAcceGraph.take();
            }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dataQ.add(data);
            executor.execute(this);
        }
    }

    public void executeGraph() {
        //-- Prepare Executor Services
        executor = Executors.newCachedThreadPool();
        addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        //-- Prepare Timeline
        prepareTimeline();
    }
    

}
