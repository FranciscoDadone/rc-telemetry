package com.franciscodadone.controller;

import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;

public class ArduinoHandler {

    public static SerialPort serialPort;
    public static int gyX;
    public static int gyY;

    public static boolean connect() {
        SerialPort[] AvailablePorts = SerialPort.getCommPorts();

        serialPort = AvailablePorts[0];

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
        serialPort.closePort();
    }

    public static void startReading() {
        new Thread(() -> {
            try {
                while (true) {

                    byte[] readBuffer = new byte[100];
                    serialPort.readBytes(readBuffer, readBuffer.length);

                    String S = new String(readBuffer, "UTF-8");

                    for (String s : S.lines().toList()) {
                        if (s.startsWith(";") && s.endsWith(";") && (s.length() >= 10)) {
                            s = s.replace(";", "");
                            Object[] arr = Arrays.stream(s.split(" ")).toArray();
                            gyX = Integer.valueOf((String) arr[0]);
                            gyY = Integer.valueOf((String) arr[1]);
//                            updateHorizon();
                            break;
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
