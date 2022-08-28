package com.franciscodadone.controller;

import com.franciscodadone.gui.CalibrationFrame;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Horizon;

public class CalibrationFrameController {
    private final CalibrationFrame view;

    public CalibrationFrameController(CalibrationFrame calibrationFrame) {
        this.view = calibrationFrame;
        initController();
    }

    private void initController() {
        view.getSetGyroCENTERButton().addActionListener(e -> {
            Horizon.gyCenterRollTrim = Horizon.x;
            Horizon.gyCenterPitchTrim = Horizon.y;
            Horizon.gyCenterInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        view.getSetGyroDOWNButton().addActionListener(e -> {
            Horizon.gyDownTrim = Horizon.y;
            Horizon.gyDownInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        view.getSetGyroUPButton().addActionListener(e -> {
            Horizon.gyUpTrim = Horizon.y;
            Horizon.gyUpInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        view.getSetGyroLEFTButton().addActionListener(e -> {
            Horizon.gyLeftTrim = Horizon.x;
            Horizon.gyLeftInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        view.getSetGyroRIGHTButton().addActionListener(e -> {
            Horizon.gyRightTrim = Horizon.x;
            Horizon.gyRightInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        view.getCalibrateAltitudeButton().addActionListener(e -> {
            BMP280.altitudeTrim = BMP280.altitudeFromArduino;
            ConfigurationHandler.update();
        });
    }
}
