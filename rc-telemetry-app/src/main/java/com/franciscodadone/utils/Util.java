package com.franciscodadone.utils;

import com.franciscodadone.gui.MainFrame;
import com.franciscodadone.model.Accelerometer;
import com.franciscodadone.model.BMP280;
import com.franciscodadone.model.Horizon;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;

import java.awt.*;

public class Util {
    public static void updateHorizon() {
        int tempGyX = Horizon.x;
        int tempGyY = Horizon.y;
        int tempGyZ = Horizon.z;

        if (Horizon.gyLeftInvertedTrim != 0) tempGyZ = (Math.abs(Horizon.z) * -90) / Horizon.gyLeftInvertedTrim;

        if (Horizon.gyLeftTrim != 0 && Horizon.x < 0) tempGyX = (Math.abs(Horizon.x) * 90) / Horizon.gyLeftTrim;
        if (Horizon.gyRightTrim != 0 && Horizon.x >= 0) tempGyX = (Math.abs(Horizon.x) * 90) / Horizon.gyRightTrim;

        if (Horizon.gyDownTrim != 0 && Horizon.y >= 0) tempGyY = (Math.abs(Horizon.y) * -55) / Horizon.gyDownTrim;
        if (Horizon.gyUpTrim != 0 && Horizon.y < 0) tempGyY = (Math.abs(Horizon.y) * -55) / Horizon.gyUpTrim;

        boolean isInverted = tempGyZ > 90;

        MainFrame.ah.setAttitude((isInverted) ? -tempGyX - 180 : tempGyX, (tempGyY != 0) ? tempGyY : 1);
    }

    public static void updateAltimeter() {
        MainFrame.altimeter.setSpeed(BMP280.altitude);
    }

    public static void updateGForce() {
        MainFrame.gForce.setSpeed(Accelerometer.maxZ / 1000);
    }

    public static void updateTemperature() {
        MainFrame.temperature.setSpeed(BMP280.temperature);
    }

    public static void updatePressure() {
        MainFrame.pressure.setSpeed(BMP280.pressure);
    }

    public static JFreeChart createChart(XYDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.setTitle(new TextTitle(title,
                        new Font("Serif", java.awt.Font.BOLD, 10)
                )
        );
        return chart;
    }
}
