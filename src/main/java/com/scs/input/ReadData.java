package com.scs.input;

import java.io.*;

import static com.scs.variables.ControlVariables.*;
import static com.scs.variables.DataVariables.*;

public class ReadData extends Thread {
    private boolean isInput;

    public ReadData() {
        isInput = false;
    }

    public boolean checkInput() {
        File dataCSV = new File(getClass().getResource("/data.csv").getFile());
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(dataCSV));
            isInput = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isInput = false;
        }
        return isInput;
    }

    public void run() {
        File dataCSV = new File(getClass().getResource("/data.csv").getFile());
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(dataCSV));
            String data;

            while (dataGenerate && (data = reader.readLine()) != null) {
                acceGraph.add(data);
                int[] loc = new int[5];
                int j = 0;
                for (int i = -1; (i = data.indexOf(",", i + 1)) != -1; i++) {
                    loc[j] = i;
                    j++;
                }
                acceXGraph.add(Double.parseDouble(data.substring(loc[1] + 1, loc[2])));
                acceYGraph.add(Double.parseDouble(data.substring(loc[2] + 1, loc[3])));
                acceZGraph.add(Double.parseDouble(data.substring(loc[3] + 1, loc[4])));
                totalAcceGraph.add(Double.parseDouble(data.substring(loc[4] + 1)));
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}