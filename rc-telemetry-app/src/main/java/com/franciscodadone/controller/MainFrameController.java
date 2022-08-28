package com.franciscodadone.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.franciscodadone.gui.CalibrationFrame;
import com.franciscodadone.gui.MainFrame;
import com.franciscodadone.model.Accelerometer;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Horizon;
import com.franciscodadone.utils.Global;

import javax.swing.*;
import java.time.LocalTime;
import java.util.Arrays;

public class MainFrameController {
    private final MainFrame view;

    public MainFrameController(MainFrame mainFrame) {
        this.view = mainFrame;
        initController();
    }

    @SuppressWarnings("BusyWait")
    private void initController() {
        view.getCalibrateButton().addActionListener(e -> new CalibrationFrame());
        view.getResetGraphsButton().addActionListener(e -> resetGraphs());
        view.getConnectButton().addActionListener((e) -> connectArduino());

        Object[] ports = ArduinoHandler.getPorts();
        for (Object port : ports) view.getComPortComboBox().addItem(port);

        new Thread(() -> {
            int i = -1;
            int i2 = -1;
            while (true) {
                if (Global.appStarted) {
                    i++;
                    i2++;
                    if (i != 0) {
                        view.getTemperatureSeries().add(i, BMP280.temperature);
                        view.getAltitudeSeries().add(i, (int) BMP280.altitude);
                        view.getAccelerometerMaxSeries().add(i, Accelerometer.maxZ);

                        if (Accelerometer.maxGForceRegistered < Accelerometer.maxZ) Accelerometer.maxGForceRegistered = Accelerometer.maxZ;
                        view.getMaxGLabel().setText("Max Gs: " + Accelerometer.maxGForceRegistered + "G");

                        if (BMP280.maxAltitudeRegistered < BMP280.altitude) BMP280.maxAltitudeRegistered = BMP280.altitude;
                        view.getMaxAltitudeLabel().setText("Max Altitude: " + BMP280.maxAltitudeRegistered + "m");

                        view.getFlightTimeLabel().setText("Flight time: " + LocalTime.ofSecondOfDay(Global.flightTime));

                        if (i2 == 60) {
                            view.getAltitudeSeries().remove(0);
                            view.getTemperatureSeries().remove(0);
                            view.getAccelerometerMaxSeries().remove(0);
                            i2 = 59;
                        }
                        Accelerometer.maxZ = 0;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


        new Thread(() -> {
            while(true) {
                if (Global.appStarted) {
                    updateHorizon();
                    updateAltimeter();
                    updateGForce();
                    updateTemperature();
                    updatePressure();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void resetGraphs() {
        view.getTemperatureSeries().clear();
        view.getAltitudeSeries().clear();
        view.getAccelerometerMaxSeries().clear();

        Accelerometer.maxGForceRegistered = 0;
        BMP280.maxAltitudeRegistered = 0;
    }

    private void connectArduino() {
        ArduinoHandler.disconnect();
        if (view.getComPortComboBox().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "No COM port selected!","Error",  JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean started = ArduinoHandler.connect((SerialPort)view.getComPortComboBox().getSelectedItem());
        if (!started) return;
        ArduinoHandler.startReading();
        Global.appStarted = true;
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

        view.getAh().setAttitude((isInverted) ? -tempGyX - 180 : tempGyX, (tempGyY != 0) ? tempGyY : 1);
    }

    private void updateAltimeter() {
        view.getAltimeter().setSpeed(BMP280.altitude);
    }

    private void updateGForce() {
        view.getGForce().setSpeed((Accelerometer.maxZ / 1000) + 1);
    }

    private void updateTemperature() {
        view.getTemperature().setSpeed(BMP280.temperature);
    }

    private void updatePressure() {
        view.getPressure().setSpeed(BMP280.pressure);
    }
}
