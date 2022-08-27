package com.franciscodadone.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.franciscodadone.model.Accelerometer;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Horizon;
import com.franciscodadone.utils.Global;

import java.util.Arrays;

public class ArduinoHandler {

    public static SerialPort serialPort;

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
        Global.appStarted = false;
        serialPort.closePort();
    }

    public static void startReading() {
        new Thread(() -> {
            try {
                while (true) {
                    if (Global.appStarted) {
                        byte[] readBuffer = new byte[100];
                        serialPort.readBytes(readBuffer, readBuffer.length);

                        String S = new String(readBuffer, "UTF-8");

                        for (String s : S.lines().toList()) {
                            if (s.startsWith(";") && s.endsWith(";") && (s.length() >= 10)) {
                                s = s.replace(";", "");
                                Object[] arr = Arrays.stream(s.split(" ")).toArray();

                                // Horizon
                                Horizon.x = Integer.valueOf((String) arr[0]);
                                Horizon.y = Integer.valueOf((String) arr[1]);
                                Horizon.z = Integer.valueOf((String) arr[2]);

                                Horizon.x -= Horizon.gyCenterRollTrim;
                                Horizon.y -= Horizon.gyCenterPitchTrim;
                                Horizon.z -= Horizon.gyCenterInvertedTrim;
                                // END Horizon

                                // Accelerometer
                                Accelerometer.z = Integer.valueOf((String) arr[6]);
                                if (Math.abs(Accelerometer.z) > Accelerometer.maxZ) Accelerometer.maxZ = Math.abs(Accelerometer.z);
                                // END Accelerometer

                                // BMP280
                                BMP280.altitude = Float.valueOf((String) arr[3]);
                                BMP280.pressure = Float.valueOf((String) arr[4]);
                                BMP280.temperature = Float.valueOf((String) arr[5]);

                                BMP280.altitudeFromArduino = BMP280.altitude;
                                BMP280.altitude -= BMP280.altitudeTrim;

                                if (BMP280.altitude < 0) BMP280.altitude = 0;
                                // END BMP280

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
