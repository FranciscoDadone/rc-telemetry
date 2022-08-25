package com.franciscodadone.gui;

import com.franciscodadone.controller.ArduinoHandler;
import com.franciscodadone.controller.ConfigurationHandler;
import com.franciscodadone.model.Horizon;
import com.github.kkieffer.jcirculargauges.JArtificialHorizonGauge;
import com.github.kkieffer.jcirculargauges.JCompass;
import com.github.kkieffer.jcirculargauges.JSpeedometer;

import javax.swing.*;
import java.awt.*;
public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel graphsPanel;
    private JPanel horizonPanel;
    private JPanel subPanelIzq;
    private JPanel subPanelDer;
    private JPanel speedPanel;
    private JPanel heightPanel;
    private JPanel compassPanel;
    private JPanel gForcePanel;
    private JButton setGyroCENTERButton;
    private JButton setGyroRIGHTButton;
    private JButton setGyroLEFTButton;
    private JButton setGyroUPButton;
    private JButton setGyroDOWNButton;



    private JArtificialHorizonGauge ah;

    public MainFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setTitle("RC Telemetry");
        this.setContentPane(mainPanel);

        ah = new JArtificialHorizonGauge(1.5);
        ah.setColors(Color.WHITE, new Color(0, 50, 200), new Color(100, 45, 30), new Color(0, 0, 100));
        horizonPanel.add(ah);
        ah.setAttitude(1, 1);

        JCompass g = new JCompass(false);
        g.setColors(Color.WHITE, Color.YELLOW, null, Color.BLACK);
        g.setCourse(0);
        compassPanel.add(g);


        JSpeedometer speedometer = new JSpeedometer(10, "km");
        speedometer.setColors(Color.RED, null, Color.BLACK);
        speedometer.setSpeed(0);
        speedPanel.add(speedometer);

        this.pack();

        setGyroCENTERButton.addActionListener(e -> {
            Horizon.gyCenterRollTrim = Horizon.x;
            Horizon.gyCenterPitchTrim = Horizon.y;
            Horizon.gyCenterInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        setGyroDOWNButton.addActionListener(e -> {
            Horizon.gyDownTrim = Horizon.y;
            Horizon.gyDownInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        setGyroUPButton.addActionListener(e -> {
            Horizon.gyUpTrim = Horizon.y;
            Horizon.gyUpInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        setGyroLEFTButton.addActionListener(e -> {
            Horizon.gyLeftTrim = Horizon.x;
            Horizon.gyLeftInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        setGyroRIGHTButton.addActionListener(e -> {
            Horizon.gyRightTrim = Horizon.x;
            Horizon.gyRightInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        new Thread(() -> {
            while(true) {
                updateHorizon();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void updateHorizon() {
        int tempGyX = Horizon.x;
        int tempGyY = Horizon.y;
        int tempGyZ = Horizon.z;

        if (Horizon.gyLeftInvertedTrim != 0) tempGyZ = (Math.abs(Horizon.z) * -90) / Horizon.gyLeftInvertedTrim;

        if (Horizon.gyLeftTrim != 0 && Horizon.x < 0) tempGyX = (Math.abs(Horizon.x) * 90) / Horizon.gyLeftTrim;
        if (Horizon.gyRightTrim != 0 && Horizon.x >= 0) tempGyX = (Math.abs(Horizon.x) * 90) / Horizon.gyRightTrim;

        if (Horizon.gyDownTrim != 0 && Horizon.y >= 0) tempGyY = (Math.abs(Horizon.y) * -55) / Horizon.gyDownTrim;
        if (Horizon.gyUpTrim != 0 && Horizon.y < 0) tempGyY = (Math.abs(Horizon.y) * -55) / Horizon.gyUpTrim;

        boolean isInverted = tempGyZ > 90;

        ah.setAttitude((isInverted) ? -tempGyX - 180 : tempGyX, (tempGyY != 0) ? tempGyY : 1);
    }
}
