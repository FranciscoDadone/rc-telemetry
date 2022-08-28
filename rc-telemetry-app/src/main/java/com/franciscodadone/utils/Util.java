package com.franciscodadone.utils;

import com.franciscodadone.Main;
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
