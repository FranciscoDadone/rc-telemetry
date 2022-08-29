package com.franciscodadone.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.franciscodadone.gui.CompassCalibrationFrame;
import com.franciscodadone.model.Accelerometer;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Compass;
import com.franciscodadone.model.Horizon;
import com.franciscodadone.utils.AppStatus;
import com.franciscodadone.utils.Global;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ArduinoHandler {

    private static SerialPort serialPort;
    private static CompassCalibrationFrame calibrationFrame;

    public static Object[] getPorts() {
        return Arrays.stream(SerialPort.getCommPorts()).toArray();
    }

    public static boolean connect(SerialPort port) {
        serialPort = port;

        int BaudRate = 9600;
        int DataBits = 8;
        int StopBits = SerialPort.ONE_STOP_BIT;
        int Parity   = SerialPort.NO_PARITY;

        serialPort.setComPortParameters(BaudRate,
                DataBits,
                StopBits,
                Parity);

        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING,
                1000,
                0);

        return serialPort.openPort();
    }

    public static void disconnect() {
        if (serialPort == null) return;
        Global.appStatus = AppStatus.PRE_START;
        serialPort.closePort();
    }

    public static void startReading() {
        new Thread(() -> {
            try {
                while (true) {
                    if (Global.appStatus.equals(AppStatus.STARTED) || Global.appStatus.equals(AppStatus.CALIBRATING)) {
                        byte[] readBuffer = new byte[100];
                        serialPort.readBytes(readBuffer, readBuffer.length);

                        String S = new String(readBuffer, StandardCharsets.UTF_8);

                        if (Global.appStatus.equals(AppStatus.CALIBRATING) && S.lines().toList().get(0).startsWith(";")) {
                            calibrationFrame.setVisible(false);
                            Global.appStatus = AppStatus.STARTED;
                        }

                        for (String s : S.lines().toList()) {
                            if (s.contains("calibration")) {
                                calibrationFrame = new CompassCalibrationFrame();

                                Global.appStatus = AppStatus.CALIBRATING;
                                break;
                            } else if (s.startsWith(";") && s.endsWith(";") && (s.length() >= 10)) {
                                s = s.replace(";", "");
                                Object[] arr = Arrays.stream(s.split(" ")).toArray();

                                // Horizon
                                Horizon.x = Integer.parseInt((String) arr[0]);
                                Horizon.y = Integer.parseInt((String) arr[1]);
                                Horizon.z = Integer.parseInt((String) arr[2]);

                                Horizon.x -= Horizon.gyCenterRollTrim;
                                Horizon.y -= Horizon.gyCenterPitchTrim;
                                Horizon.z -= Horizon.gyCenterInvertedTrim;
                                // END Horizon

                                // BMP280
                                BMP280.altitude = Float.parseFloat((String) arr[3]);
                                BMP280.pressure = Float.parseFloat((String) arr[4]);
                                BMP280.temperature = Float.parseFloat((String) arr[5]);

                                BMP280.altitudeFromArduino = BMP280.altitude;
                                BMP280.altitude -= BMP280.altitudeTrim;

                                if (BMP280.altitude < 0) BMP280.altitude = 0;
                                // END BMP280

                                // Accelerometer
                                Accelerometer.z = Float.parseFloat((String) arr[6]);
                                if (Math.abs(Accelerometer.z) > Accelerometer.maxZ) Accelerometer.maxZ = (Math.abs(Accelerometer.z));
                                if (Accelerometer.maxGForceRegistered < Accelerometer.maxZ) Accelerometer.maxGForceRegistered = Accelerometer.maxZ;
                                // END Accelerometer

                                Global.flightTime = Integer.parseInt((String) arr[7]);

                                // Compass
                                double tmpHeading = Double.parseDouble((String) arr[8]);
                                Compass.heading = (tmpHeading < 0 ) ? tmpHeading + 360 : tmpHeading ;
                                // END Compass

                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
