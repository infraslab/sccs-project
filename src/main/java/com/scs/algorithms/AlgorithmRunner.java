package com.scs.algorithms;

import com.scs.db.oracle.DBConnector;
import com.scs.utils.EmailSender;
import com.scs.variables.DataVariables;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.scs.variables.ControlVariables.fallDetected;
import static com.scs.variables.ControlVariables.startAlgo;
import static com.scs.variables.DataVariables.acceGraph;
import static com.scs.variables.DataVariables.userEmail;

public class AlgorithmRunner extends Thread {
    private double utv;
    private double ltv;

    public AlgorithmRunner(double utv, double ltv) {
        this.utv = utv;
        this.ltv = ltv;
    }

    private void runAlgorithm() {
        boolean isUtv = false;
        boolean isLtv = false;
        try {
            for (int k = 0; k < 20; k++) {
                String data = acceGraph.take();
                int[] loc = new int[5];
                int j = 0;
                for (int i = -1; (i = data.indexOf(",", i + 1)) != -1; i++) {
                    loc[j] = i;
                    j++;
                }
                double acceX = Double.parseDouble(data.substring(loc[1] + 1, loc[2]));
                double acceY = Double.parseDouble(data.substring(loc[2] + 1, loc[3]));
                double acceZ = Double.parseDouble(data.substring(loc[3] + 1, loc[4]));
                double acce = Double.parseDouble(data.substring(loc[4] + 1));
//                System.out.println(acce);
                if (acce >= utv) {
                    isUtv = true;
                }
                if (acce <= ltv) {
                    isLtv = true;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (isLtv && isUtv) {
            System.out.println("Fall Detected");
            fallDetected = true;
            startAlgo = false;
        }
    }

    public void run() {
        while (true) {
            if (startAlgo) {
                runAlgorithm();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (fallDetected) {
                SoundPlay soundPlay = new SoundPlay();
                soundPlay.start();

                JOptionPane.showMessageDialog(new Frame(),
                        "*!*!*!*!*!*!*! Fall Detected *!*!*!*!*!*!*!");
                DBConnector.reset();
                Platform.exit();
                System.exit(0);
//                fallDetected = false;
            }
        }
    }

    class SoundPlay extends Thread {
        public void run() {
            while (true) {
                try {
                    String bip = "fall1.m4a";
                    Media hit = new Media(new File(bip).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(hit);
                    mediaPlayer.play();
                    Thread.sleep(15000);
                    EmailSender emailSender = new EmailSender();
                    String body = "username: " + DataVariables.userName + "<br>\n" +
                            "*!*!*!*!*!*!*! Fall Detected *!*!*!*!*!*!*!";
                    emailSender.createEmailMessage(userEmail, "Fall Detected", body);
                    emailSender.sendEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
