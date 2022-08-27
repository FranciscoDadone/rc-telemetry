package com.franciscodadone.gui;

import com.franciscodadone.controller.ConfigurationHandler;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Horizon;

import javax.swing.*;
import java.awt.*;

public class CalibrationFrame extends JFrame {
    private JButton setGyroUPButton;
    private JButton setGyroLEFTButton;
    private JButton setGyroCENTERButton;
    private JButton setGyroRIGHTButton;
    private JButton setGyroDOWNButton;
    private JPanel mainPanel;
    private JButton calibrateAltitudeButton;

    public CalibrationFrame() {

        this.setSize(new Dimension(500, 200));
        this.setMinimumSize(new Dimension(300, 200));
        this.setVisible(true);
        this.setTitle("RC Telemetry - Calibration");
        this.setContentPane(mainPanel);

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

        calibrateAltitudeButton.addActionListener(e -> {
            BMP280.altitudeTrim = BMP280.altitudeFromArduino;
            ConfigurationHandler.update();
        });
    }
}
