package com.waughrn2.ca4006.graphics;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by iNfecteD on 26/03/2017.
 */
public class ParkingSlotDisplay extends JPanel{
    //Indicate the row and column of this cell in the board
    private int GridRow;
    private int GridColumn;


    public ParkingSlotDisplay(int GridRow, int GridColumn, Color color) {

        this.GridRow = GridRow;
        this.GridColumn = GridColumn;

        setBorder(new LineBorder(Color.lightGray, 1));   // Set cell's border
        setBackground(color);
    }

    public void updateSlot(Color color){
        setBackground(color);
    }
}
