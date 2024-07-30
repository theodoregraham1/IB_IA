package GUI;

import javax.swing.*;

public class GuiMain {
    public static void main(String[] args) {
        try {
            // Set the look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the Rube-Goldberg machine
        new BoardSelector();
    }
}
