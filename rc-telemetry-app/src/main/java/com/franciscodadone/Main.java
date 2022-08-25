package com.franciscodadone;

import com.franciscodadone.controller.ArduinoHandler;
import com.franciscodadone.gui.MainFrame;

public class Main {
    public static void main(String[] args) {
        ArduinoHandler.connect();
        ArduinoHandler.startReading();
        new MainFrame();
    }
}