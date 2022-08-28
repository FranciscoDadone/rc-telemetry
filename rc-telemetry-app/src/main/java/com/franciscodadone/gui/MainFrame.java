package com.franciscodadone.gui;

import com.fazecast.jSerialComm.SerialPort;
import com.franciscodadone.controller.MainFrameController;
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
    private JPanel horizonPanel;
    private JPanel altimeterPanel;
    private JPanel gPanel;
    private JPanel compassPanel;
    private JPanel temperatureGraphPanel;
    private JPanel pressureGraphPanel;
    private JPanel accelerometerGraphPanel;
    private JPanel temperaturePanel;
    private JButton calibrateButton;
    private JComboBox<Object> comPortComboBox;
    private JButton connectButton;
    private JPanel pressurePanel;
    private JButton resetGraphsButton;
    private JLabel maxGLabel;
    private JLabel maxAltitudeLabel;
    private JLabel flightTimeLabel;
    private final JArtificialHorizonGauge ah;
    private final JCompass compass;
    private final JSpeedometer altimeter;
    private final JSpeedometer gForce;
    private final JEmptyGauge temperature;
    private final JEmptyGauge pressure;
    private final XYSeries temperatureSeries;
    private final XYSeries altitudeSeries;
    private final XYSeries accelerometerMaxSeries;

    public MainFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setMinimumSize(new Dimension(1100, 700));
        this.setTitle("RC Telemetry");
        this.setContentPane(mainPanel);

        Object[] ports = Arrays.stream(SerialPort.getCommPorts()).toArray();
        for (Object port : ports) comPortComboBox.addItem(port);

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


        // Temperature chart
        temperatureSeries = new XYSeries("t");
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
        altitudeSeries = new XYSeries("a");
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
        accelerometerMaxSeries = new XYSeries("accMax");
        XYSeriesCollection accelerometerDataset = new XYSeriesCollection();
        accelerometerDataset.addSeries(accelerometerMaxSeries);
        ChartPanel accelerometerChartPanel = new ChartPanel(Util.createChart(accelerometerDataset, "Accelerometer", "Time", "Gs"));
        accelerometerChartPanel.validate();
        accelerometerChartPanel.setPreferredSize(accelerometerGraphPanel.getPreferredSize());
        accelerometerGraphPanel.add(accelerometerChartPanel, BorderLayout.CENTER);
        accelerometerGraphPanel.repaint();
        accelerometerGraphPanel.validate();
        // END Accelerometer

        new MainFrameController(this);
    }

    public JButton getCalibrateButton() {
        return calibrateButton;
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public JButton getResetGraphsButton() {
        return resetGraphsButton;
    }

    public JArtificialHorizonGauge getAh() {
        return ah;
    }

    public JSpeedometer getAltimeter() {
        return altimeter;
    }

    public JSpeedometer getGForce() {
        return gForce;
    }

    public JEmptyGauge getTemperature() {
        return temperature;
    }

    public JEmptyGauge getPressure() {
        return pressure;
    }

    public XYSeries getTemperatureSeries() {
        return temperatureSeries;
    }

    public XYSeries getAltitudeSeries() {
        return altitudeSeries;
    }

    public XYSeries getAccelerometerMaxSeries() {
        return accelerometerMaxSeries;
    }

    public JComboBox<Object> getComPortComboBox() {
        return comPortComboBox;
    }
}
