package com.franciscodadone.gui;

import javax.swing.*;
import java.awt.*;

public class CompassCalibrationFrame extends JFrame {
    private JPanel mainPanel;

    public CompassCalibrationFrame() {
        this.setVisible(true);
        this.setMinimumSize(new Dimension(600, 400));
        this.setTitle("RC Telemetry - Compass Calibration");
        this.setContentPane(mainPanel);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }
}
