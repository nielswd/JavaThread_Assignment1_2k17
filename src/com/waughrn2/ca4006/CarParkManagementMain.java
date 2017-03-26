package com.waughrn2.ca4006;

import javax.swing.*;

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