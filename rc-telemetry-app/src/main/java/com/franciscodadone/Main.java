package com.franciscodadone;

import com.franciscodadone.controller.ArduinoHandler;
import com.franciscodadone.controller.ConfigurationHandler;
import com.franciscodadone.gui.MainFrame;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (new File("configuration.yml").exists()) ConfigurationHandler.read();

        ArduinoHandler.connect();
        ArduinoHandler.startReading();
        new MainFrame();
    }
}