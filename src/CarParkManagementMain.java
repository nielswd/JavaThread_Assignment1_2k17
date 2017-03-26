import javax.swing.*;

class CarParkManagementMain {
    public static void main(String[] args) {
        GuiRunnable guiRunnable = new GuiRunnable();
        SwingUtilities.invokeLater(guiRunnable);
    }
}