package com.franciscodadone.gui;

import com.franciscodadone.controller.CalibrationFrameController;
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
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        new CalibrationFrameController(this);
    }

    public JButton getSetGyroUPButton() {
        return setGyroUPButton;
    }

    public JButton getSetGyroLEFTButton() {
        return setGyroLEFTButton;
    }

    public JButton getSetGyroCENTERButton() {
        return setGyroCENTERButton;
    }

    public JButton getSetGyroRIGHTButton() {
        return setGyroRIGHTButton;
    }

    public JButton getSetGyroDOWNButton() {
        return setGyroDOWNButton;
    }

    public JButton getCalibrateAltitudeButton() {
        return calibrateAltitudeButton;
    }
}
