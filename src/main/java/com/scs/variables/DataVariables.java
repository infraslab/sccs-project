package com.scs.variables;

import java.util.concurrent.LinkedBlockingQueue;

public class DataVariables {
    public static volatile LinkedBlockingQueue<String> acceGraph = new LinkedBlockingQueue<>();
    public static volatile LinkedBlockingQueue<Double> acceXGraph = new LinkedBlockingQueue<>();
    public static volatile LinkedBlockingQueue<Double> acceYGraph = new LinkedBlockingQueue<>();
    public static volatile LinkedBlockingQueue<Double> acceZGraph = new LinkedBlockingQueue<>();
    public static volatile LinkedBlockingQueue<Double> totalAcceGraph = new LinkedBlockingQueue<>();
    public static volatile String userName;
    public static volatile String userEmail;
}
