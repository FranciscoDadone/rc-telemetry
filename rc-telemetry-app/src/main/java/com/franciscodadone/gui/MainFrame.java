package com.franciscodadone.gui;

import com.fazecast.jSerialComm.SerialPort;
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

    private int gyX;
    private int gyY;
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


        // Max pitch 55
        // Min pitch -55

        JCompass g = new JCompass(false);
        g.setColors(Color.WHITE, Color.YELLOW, null, Color.BLACK);
        g.setCourse(0);
        compassPanel.add(g);


        JSpeedometer speedometer = new JSpeedometer(10, "km");
        speedometer.setColors(Color.RED, null, Color.BLACK);
        speedometer.setSpeed(0);
        speedPanel.add(speedometer);

        this.pack();

        SerialPort [] AvailablePorts = SerialPort.getCommPorts();

        SerialPort MySerialPort = AvailablePorts[0];

        int BaudRate = 9600;
        int DataBits = 8;
        int StopBits = SerialPort.ONE_STOP_BIT;
        int Parity   = SerialPort.NO_PARITY;

        MySerialPort.setComPortParameters(BaudRate,
                DataBits,
                StopBits,
                Parity);

        MySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING,
                1000,
                0);

        MySerialPort.openPort();

        setGyroCENTERButton.addActionListener(e -> {
            gyCenterHorizontalTrim = gyX;
            gyCenterVerticalTrim = gyY;
        });

        setGyroDOWNButton.addActionListener(e -> {
            gyDownTrim = gyY;
        });

        setGyroUPButton.addActionListener(e -> {
            gyUpTrim = gyY;
        });

        setGyroLEFTButton.addActionListener(e -> {
            gyLeftTrim = gyX;
        });

        setGyroRIGHTButton.addActionListener(e -> {
            gyRightTrim = gyX;
        });

        try {
            while (true) {

                byte[] readBuffer = new byte[100];
                MySerialPort.readBytes(readBuffer, readBuffer.length);

                String S = new String(readBuffer, "UTF-8");

                for (String s : S.lines().toList()) {
                    if (s.startsWith(";") && s.endsWith(";") && (s.length() >= 10)) {
                        s = s.replace(";", "");
                        Object[] arr = Arrays.stream(s.split(" ")).toArray();
                        gyX = Integer.valueOf((String) arr[0]);
                        gyY = Integer.valueOf((String) arr[1]);
//                        System.out.println(Arrays.toString(Arrays.stream(s.split(" ")).toArray()));
                        updateHorizon();
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        MySerialPort.closePort();
    }

    private void updateHorizon() {
        gyX -= gyCenterHorizontalTrim;
        gyY -= gyCenterHorizontalTrim;
        if (gyLeftTrim != 0 && gyX < 0) gyX = (gyX * -90) / gyLeftTrim;
        if (gyRightTrim != 0 && gyX >= 0) gyX = (gyX * 90) / gyRightTrim;


        System.out.println(gyY);
//        System.out.println(gyDownTrim);
        if (gyDownTrim != 0 && gyY >= 0) gyY = (gyY * 55) / gyDownTrim;
        if (gyUpTrim != 0 && gyY < 0) gyY = (gyY * -55) / gyUpTrim;

        System.out.println(gyY);
        System.out.println(gyDownTrim);
        System.out.println(gyUpTrim);
        System.out.println("");
        ah.setAttitude(gyX, (gyY != 0) ? gyY : 1);
    }
}
