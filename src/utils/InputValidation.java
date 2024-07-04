package utils;

import javax.swing.*;

public class InputValidation {
    public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Class<? extends JFrame> getPage(Object selectedAnchor) {
        if (!(selectedAnchor instanceof String)) {
            return null;
        }
        String choice = (String) selectedAnchor;

    }

    public enum Pages {

    }
}
