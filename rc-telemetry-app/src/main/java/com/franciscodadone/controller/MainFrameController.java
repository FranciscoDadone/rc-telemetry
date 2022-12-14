package com.franciscodadone.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.franciscodadone.gui.CalibrationFrame;
import com.franciscodadone.gui.MainFrame;
import com.franciscodadone.model.Accelerometer;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Compass;
import com.franciscodadone.model.Horizon;
import com.franciscodadone.utils.AppStatus;
import com.franciscodadone.utils.Global;

import javax.swing.*;
import java.text.DecimalFormat;
import java.time.LocalTime;

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
        view.getConnectButton().addActionListener(e -> connectArduino());

        Object[] ports = ArduinoHandler.getPorts();
        for (Object port : ports) view.getComPortComboBox().addItem(port);

        DecimalFormat df = new DecimalFormat("#.#");

        new Thread(() -> {
            int i = -1;
            int i2 = -1;
            while (true) {
                if (Global.appStatus.equals(AppStatus.STARTED)) {
                    i++;
                    i2++;
                    if (i != 0) {
                        view.getTemperatureSeries().add(i, BMP280.temperature);
                        view.getAltitudeSeries().add(i, (int) BMP280.altitude);
                        view.getAccelerometerMaxSeries().add(i, Accelerometer.maxZ);
                        view.getPressureSeries().add(i, BMP280.pressure);

                        view.getMaxGLabel().setText("Max Gs: " + df.format(Accelerometer.maxGForceRegistered) + "G");

                        if (BMP280.maxAltitudeRegistered < BMP280.altitude) BMP280.maxAltitudeRegistered = BMP280.altitude;
                        view.getMaxAltitudeLabel().setText("Max Altura: " + df.format(BMP280.maxAltitudeRegistered) + "m");

                        view.getFlightTimeLabel().setText("T. Vuelo: " + LocalTime.ofSecondOfDay(Global.flightTime));

                        if (i2 == 60) {
                            view.getAltitudeSeries().remove(0);
                            view.getTemperatureSeries().remove(0);
                            view.getAccelerometerMaxSeries().remove(0);
                            i2 = 59;
                        }
                        Accelerometer.maxZ = 1;
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
                if (Global.appStatus.equals(AppStatus.STARTED)) {
                    updateHorizon();
                    updateAltimeter();
                    updateGForce();
                    updateTemperature();
                    updatePressure();
                    updateCompass();
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
        view.getPressureSeries().clear();

        Accelerometer.maxGForceRegistered = 0;
        BMP280.maxAltitudeRegistered = 0;
    }

    private void connectArduino() {
        ArduinoHandler.disconnect();
        if (view.getComPortComboBox().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Ning??n puerto COM seleccionado!","Error",  JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean started = ArduinoHandler.connect((SerialPort)view.getComPortComboBox().getSelectedItem());
        if (!started) return;
        ArduinoHandler.startReading();
        Global.appStatus = AppStatus.STARTED;
    }

    private void updateHorizon() {
        int tempGyX = Horizon.x;
        int tempGyY = Horizon.y;

        if (Horizon.gyLeftTrim != 0 && Horizon.x < 0) tempGyX = (Math.abs(Horizon.x) * 90) / Horizon.gyLeftTrim;
        if (Horizon.gyRightTrim != 0 && Horizon.x >= 0) tempGyX = (Math.abs(Horizon.x) * 90) / Horizon.gyRightTrim;

        if (Horizon.gyDownTrim != 0 && Horizon.y >= 0) tempGyY = (Math.abs(Horizon.y) * -55) / Horizon.gyDownTrim;
        if (Horizon.gyUpTrim != 0 && Horizon.y < 0) tempGyY = (Math.abs(Horizon.y) * -55) / Horizon.gyUpTrim;

        boolean isInverted = Horizon.z >= 0;

        view.getAh().setAttitude((isInverted) ? -tempGyX - 180 : tempGyX, (tempGyY != 0) ? tempGyY : 1);
    }

    private void updateAltimeter() {
        view.getAltimeter().setSpeed(BMP280.altitude);
    }

    private void updateGForce() {
        view.getGForce().setSpeed(Accelerometer.maxZ);
    }

    private void updateTemperature() {
        view.getTemperature().setSpeed(BMP280.temperature);
    }

    private void updatePressure() {
        view.getPressure().setSpeed(BMP280.pressure);
    }

    private void updateCompass() {
        view.getCompass().setCourse(Compass.heading);
    }
}
