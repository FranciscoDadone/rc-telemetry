package com.franciscodadone;

import com.franciscodadone.controller.ConfigurationHandler;
import com.franciscodadone.gui.MainFrame;
import com.franciscodadone.utils.AppStatus;
import com.franciscodadone.utils.Global;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Global.appStatus = AppStatus.PRE_START;
        if (new File("configuration.yml").exists()) ConfigurationHandler.read();
        new MainFrame();
    }
}