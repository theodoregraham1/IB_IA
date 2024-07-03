package utils;

public class InputValidation {
    public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Pages getPage(Object selectedAnchor) {
        // TODO
    }

    public enum Pages {

    }
}
