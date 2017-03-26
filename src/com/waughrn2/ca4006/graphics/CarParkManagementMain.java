package com.waughrn2.ca4006.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;

/** A short example of a nested layout that can change PLAF at runtime.
 The TitledBorder of each JPanel shows the layouts explicitly set.
 @author Andrew Thompson
 @version 2011-04-12 */
class CarParkManagementMain {
    public static void main(String[] args) {
        GuiRunnable guiRunnable = new GuiRunnable();
        SwingUtilities.invokeLater(guiRunnable);
    }
}