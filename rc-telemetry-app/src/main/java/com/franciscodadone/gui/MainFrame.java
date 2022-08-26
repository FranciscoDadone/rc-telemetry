package com.franciscodadone.gui;

import com.franciscodadone.controller.ConfigurationHandler;
import com.franciscodadone.model.Accelerometer;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Horizon;
import com.franciscodadone.utils.Util;
import com.github.kkieffer.jcirculargauges.JArtificialHorizonGauge;
import com.github.kkieffer.jcirculargauges.JCompass;
import com.github.kkieffer.jcirculargauges.JSpeedometer;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
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
    private JPanel compassPanel2;


    public static JArtificialHorizonGauge ah;
    public static JCompass compass;
    public static JSpeedometer altimeter;
    public static JSpeedometer gForce;
    public static JEmptyGauge temperature;

    public MainFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setTitle("RC Telemetry");
        this.setContentPane(mainPanel);

        ah = new JArtificialHorizonGauge(1.5);
        ah.setColors(Color.WHITE, new Color(0, 0, 0), new Color(124, 69, 57), new Color(75, 113, 199));
        horizonPanel.add(ah, BorderLayout.CENTER);
        ah.setAttitude(1, 1);


        temperature = new JEmptyGauge(1, "ºC");
        temperature.setColors(Color.RED, new Color(0,0,0), new Color(16, 16, 16));
        compassPanel2.add(temperature);
        compassPanel2.setPreferredSize(new Dimension(100,100));

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
//        gPanel.setPreferredSize(gForce.getPreferredSize());
        this.pack();

        setGyroCENTERButton.addActionListener(e -> {
            Horizon.gyCenterRollTrim = Horizon.x;
            Horizon.gyCenterPitchTrim = Horizon.y;
            Horizon.gyCenterInvertedTrim = Horizon.z;
            BMP280.altitudeTrim = BMP280.altitude;
            ConfigurationHandler.update();
        });

        setGyroDOWNButton.addActionListener(e -> {
            Horizon.gyDownTrim = Horizon.y;
            Horizon.gyDownInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        setGyroUPButton.addActionListener(e -> {
            Horizon.gyUpTrim = Horizon.y;
            Horizon.gyUpInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        setGyroLEFTButton.addActionListener(e -> {
            Horizon.gyLeftTrim = Horizon.x;
            Horizon.gyLeftInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
        });

        setGyroRIGHTButton.addActionListener(e -> {
            Horizon.gyRightTrim = Horizon.x;
            Horizon.gyRightInvertedTrim = Horizon.z;
            ConfigurationHandler.update();
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


        new Thread(() -> {
            while(true) {
                Util.updateHorizon();
                Util.updateAltimeter();
                Util.updateGForce();
                Util.updateTemperature();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
