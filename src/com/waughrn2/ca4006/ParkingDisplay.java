package com.waughrn2.ca4006;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.geom.Line2D;

/**
 * Created by iNfecteD on 25/03/2017.
 */
public class ParkingDisplay extends JPanel {
    public static final int ROWS = 10;
    public static final int COL = 100;
    private static final int PREF_W = 10;
    private static final int PREF_H = 100;

    public ParkingDisplay() {
        // TODO Auto-generated constructor stub
    }

    // the method that does the drawing:
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COL; col++) {
                // choose a Color black or white depending on row and col

                Color c = Color.white;
                if (row % 2 == col % 2) {
                    c = Color.black;
                }

                // this would work too!
                // Color c = (row % 2 == col % 2) ? Color.BLACK : Color.WHITE;
                g.setColor(c);

                int x = (col * getWidth()) / ROWS;
                int y = (row * getHeight()) / ROWS;
                int w = getWidth() / ROWS;
                int h = getHeight() / ROWS;

                g.fillRect(x, y, w, h);
            }
        }
    }

    @Override  // set size of our GUI
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        return new Dimension(PREF_W, PREF_H);
    }
}
