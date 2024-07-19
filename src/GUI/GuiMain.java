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

        AnchorListener.getPage(Constants.VIEW_PAPERS);
    }
}
