package com.franciscodadone.gui;

import com.fazecast.jSerialComm.SerialPort;
import com.franciscodadone.controller.ArduinoHandler;
import com.github.kkieffer.jcirculargauges.JArtificialHorizonGauge;
import com.github.kkieffer.jcirculargauges.JCompass;
import com.github.kkieffer.jcirculargauges.JSpeedometer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

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

    private int gyCenterHorizontalTrim;
    private int gyCenterVerticalTrim;
    private int gyRightTrim;
    private int gyLeftTrim;
    private int gyUpTrim;
    private int gyDownTrim;

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



//        MySerialPort.isOpen()

        setGyroCENTERButton.addActionListener(e -> {
            gyCenterHorizontalTrim = ArduinoHandler.gyX;
            gyCenterVerticalTrim = ArduinoHandler.gyY;
        });

        setGyroDOWNButton.addActionListener(e -> {
            gyDownTrim = ArduinoHandler.gyY;
        });

        setGyroUPButton.addActionListener(e -> {
            gyUpTrim = ArduinoHandler.gyY;
        });

        setGyroLEFTButton.addActionListener(e -> {
            gyLeftTrim = ArduinoHandler.gyX;
        });

        setGyroRIGHTButton.addActionListener(e -> {
            gyRightTrim = ArduinoHandler.gyX;
        });

        new Thread(() -> {
            while(true) {
                updateHorizon();
            }
        }).start();
    }

    private void updateHorizon() {
        ArduinoHandler.gyX -= gyCenterHorizontalTrim;
        ArduinoHandler.gyY -= gyCenterHorizontalTrim;
        int tempGyX = ArduinoHandler.gyX;
        int tempGyY = ArduinoHandler.gyY;

        if (gyLeftTrim != 0 && ArduinoHandler.gyX < 0) tempGyX = (Math.abs(ArduinoHandler.gyX) * 90) / gyLeftTrim;
        if (gyRightTrim != 0 && ArduinoHandler.gyX >= 0) tempGyX = (Math.abs(ArduinoHandler.gyX) * 90) / gyRightTrim;

        if (gyDownTrim != 0 && ArduinoHandler.gyY >= 0) tempGyY = (Math.abs(ArduinoHandler.gyY) * -55) / gyDownTrim;
        if (gyUpTrim != 0 && ArduinoHandler.gyY < 0) tempGyY = (Math.abs(ArduinoHandler.gyY) * -55) / gyUpTrim;

        ah.setAttitude(tempGyX, (tempGyY != 0) ? tempGyY : 1);
    }
}
