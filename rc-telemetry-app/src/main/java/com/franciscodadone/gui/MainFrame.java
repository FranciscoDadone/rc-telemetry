package com.franciscodadone.gui;

import com.fazecast.jSerialComm.SerialPort;
import com.franciscodadone.controller.ArduinoHandler;
import com.franciscodadone.model.Accelerometer;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.utils.Global;
import com.franciscodadone.utils.Util;
import com.github.kkieffer.jcirculargauges.JArtificialHorizonGauge;
import com.github.kkieffer.jcirculargauges.JCompass;
import com.github.kkieffer.jcirculargauges.JSpeedometer;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel graphsPanel;
    private JPanel horizonPanel;
    private JPanel subPanelIzq;
    private JPanel subPanelDer;
    private JPanel altimeterPanel;
    private JPanel gPanel;
    private JPanel compassPanel;
    private JButton setGyroCENTERButton;
    private JButton setGyroRIGHTButton;
    private JButton setGyroLEFTButton;
    private JButton setGyroUPButton;
    private JButton setGyroDOWNButton;
    private JPanel temperatureGraphPanel;
    private JPanel pressureGraphPanel;
    private JPanel accelerometerGraphPanel;
    private JPanel temperaturePanel;
    private JButton calibrateButton;
    private JComboBox comPortComboBox;
    private JButton connectButton;
    private JPanel pressurePanel;
    public static JArtificialHorizonGauge ah;
    public static JCompass compass;
    public static JSpeedometer altimeter;
    public static JSpeedometer gForce;
    public static JEmptyGauge temperature;
    public static JEmptyGauge pressure;

    public MainFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setMinimumSize(new Dimension(1100, 700));
        this.setTitle("RC Telemetry");
        this.setContentPane(mainPanel);

        Object[] ports = Arrays.stream(SerialPort.getCommPorts()).toArray();
        for (int i = 0; i < ports.length; i++) {
            comPortComboBox.addItem(ports[i]);
        }

        connectButton.addActionListener((e) -> {
            ArduinoHandler.disconnect();
            boolean started = ArduinoHandler.connect((SerialPort)comPortComboBox.getSelectedItem());
            if (!started) return;
            ArduinoHandler.startReading();
            Global.appStarted = true;
        });

        ah = new JArtificialHorizonGauge(1.5);
        ah.setColors(Color.WHITE, new Color(0, 0, 0), new Color(124, 69, 57), new Color(75, 113, 199));
        horizonPanel.add(ah, BorderLayout.CENTER);
        ah.setAttitude(1, 1);


        temperature = new JEmptyGauge(1, "ºC", 9.0F);
        temperature.setColors(Color.RED, new Color(0,0,0), new Color(16, 16, 16));
        temperaturePanel.add(temperature);
        temperaturePanel.setPreferredSize(new Dimension(100,100));

        pressure = new JEmptyGauge(1, "hPa", 6.0F);
        pressure.setColors(Color.RED, new Color(0,0,0), new Color(16, 16, 16));
        pressurePanel.add(pressure);
        pressurePanel.setPreferredSize(new Dimension(100,100));

        compass = new JCompass(false);
        compass.setColors(Color.WHITE, Color.YELLOW, null, new Color(16, 16, 16));
        compassPanel.add(compass);
        compassPanel.setPreferredSize(new Dimension(100,100));

        altimeter = new JSpeedometer(100, "meters");
        altimeter.setColors(Color.RED, new Color(0,0,0), new Color(16, 16, 16));
        altimeterPanel.add(altimeter, BorderLayout.CENTER);

        gForce = new JSpeedometer(1, "Gs");
        gForce.setColors(Color.RED, new Color(0,0,0), new Color(16, 16, 16));
        gPanel.add(gForce, BorderLayout.CENTER);
        this.pack();

        calibrateButton.addActionListener(e -> {
            new CalibrationFrame();
        });


        // Temperature chart
        XYSeries temperatureSeries = new XYSeries("t");
        XYSeriesCollection temperatureDataset = new XYSeriesCollection();
        temperatureDataset.addSeries(temperatureSeries);
        ChartPanel temperatureChartPanel = new ChartPanel(Util.createChart(temperatureDataset, "Temperature", "Time", "ºC"));
        temperatureChartPanel.validate();
        temperatureChartPanel.setPreferredSize(temperatureGraphPanel.getPreferredSize());
        temperatureGraphPanel.add(temperatureChartPanel, BorderLayout.CENTER);
        temperatureGraphPanel.repaint();
        temperatureGraphPanel.validate();
        // END Temperature chart
        // Altitude chart
        XYSeries altitudeSeries = new XYSeries("a");
        XYSeriesCollection altitudeDataset = new XYSeriesCollection();
        altitudeDataset.addSeries(altitudeSeries);
        ChartPanel pressureChartPanel = new ChartPanel(Util.createChart(altitudeDataset, "Altitude", "Time", "Meters"));
        pressureChartPanel.validate();
        pressureChartPanel.setPreferredSize(pressureGraphPanel.getPreferredSize());
        pressureGraphPanel.add(pressureChartPanel, BorderLayout.CENTER);
        pressureGraphPanel.repaint();
        pressureGraphPanel.validate();
        // END Pressure chart
        // Accelerometer
        XYSeries accelerometerMaxSeries = new XYSeries("accMax");
        XYSeriesCollection accelerometerDataset = new XYSeriesCollection();
        accelerometerDataset.addSeries(accelerometerMaxSeries);
        ChartPanel accelerometerChartPanel = new ChartPanel(Util.createChart(accelerometerDataset, "Accelerometer", "Time", "Gs"));
        accelerometerChartPanel.validate();
        accelerometerChartPanel.setPreferredSize(accelerometerGraphPanel.getPreferredSize());
        accelerometerGraphPanel.add(accelerometerChartPanel, BorderLayout.CENTER);
        accelerometerGraphPanel.repaint();
        accelerometerGraphPanel.validate();
        // END Accelerometer

        new Thread(() -> {
            int i = -1;
            int i2 = -1;
            while (true) {
                if (Global.appStarted) {
                    i++;
                    i2++;
                    if (i != 0) {
                        temperatureSeries.add(i, BMP280.temperature);
                        altitudeSeries.add(i, (int) BMP280.altitude);
                        accelerometerMaxSeries.add(i, Accelerometer.maxZ);

                        if (i2 == 60) {
                            altitudeSeries.remove(0);
                            temperatureSeries.remove(0);
                            accelerometerMaxSeries.remove(0);
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
                    Util.updateHorizon();
                    Util.updateAltimeter();
                    Util.updateGForce();
                    Util.updateTemperature();
                    Util.updatePressure();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
