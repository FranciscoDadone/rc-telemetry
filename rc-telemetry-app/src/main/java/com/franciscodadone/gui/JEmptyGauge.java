package com.franciscodadone.gui;

import com.github.kkieffer.jcirculargauges.JCircularGauge;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class JEmptyGauge extends JCircularGauge {
    private static final int NUM_MAJOR_TICKS = 14;
    private double maxSpeed;
    private String unit;
    private double currentSpeed;
    private Color indicatorColor;
    private int tickIncrement;
    private float fontSize;

    public JEmptyGauge(int increment, String unit, float fontSize) {
        this.fontSize = fontSize;
        this.unit = unit;
        this.indicatorColor = Color.BLACK;
        this.dialCenterDivider = 14.0F;
        this.setIncrement(increment);
    }

    public final void setIncrement(int increment) {
        this.maxSpeed = (double)(increment * 14);
        this.tickIncrement = increment;
        this.repaint();
    }

    public int getTickIncrement() {
        return this.tickIncrement;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        this.repaint();
    }

    public String getUnit() {
        return this.unit;
    }

    public void setColors(Color indicator, Color bezelColor, Color background) {
        this.indicatorColor = indicator == null ? Color.BLACK : indicator;
        super.setColors(bezelColor, background);
    }

    public final void setSpeed(double spd) {
        this.currentSpeed = spd;
        this.repaint();
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        this.setupForPaint(g2d);
        this.paintGaugeBackground(g2d);
        g2d.setColor(this.indicatorColor);
        double angle = -180.0;
        g2d.rotate(Math.toRadians(angle));
        double speedLabel = 0.0;
        int smallTick = 10;
        int majorTickIncrement = 20;
        if (this.outsideRadius < 100) {
            majorTickIncrement = 40;
        }

        Font largeFont;
        int unitFontHeight;
        for(int i = (int)angle; i <= 100; i += smallTick) {
            if (i % majorTickIncrement == 0) {
                g2d.setStroke(new BasicStroke(4.0F));
                largeFont = g2d.getFont();
                largeFont = largeFont.deriveFont(fontSize);
                g2d.setFont(largeFont);
                String label = String.valueOf((int)Math.round(speedLabel));
                unitFontHeight = g2d.getFontMetrics().stringWidth(label);
                int fontHeight = g2d.getFontMetrics().getHeight();
                AffineTransform a = g2d.getTransform();
                g2d.rotate(Math.toRadians(-angle));
                g2d.translate(-unitFontHeight / 2, fontHeight / 2);
                g2d.setTransform(a);
                g2d.setFont(largeFont);
                speedLabel += (double)this.tickIncrement;
            }

            angle += (double)smallTick;
        }

        g2d.setTransform(this.centerGaugeTransform);

        Font origFont = g2d.getFont();
        largeFont = origFont.deriveFont((float)origFont.getSize() * 4.0F);
        g2d.setFont(largeFont);
        String label =String.valueOf((int)Math.round(this.currentSpeed));
        int fontWidth = g2d.getFontMetrics().stringWidth(label);
        g2d.drawString(label + " " + this.unit, -fontWidth, 0);
        largeFont = origFont.deriveFont((float)origFont.getSize() * 2.0F);
        g2d.setFont(largeFont);
        this.paintBezel(g2d);
        this.completePaint(g2d);
    }
}

