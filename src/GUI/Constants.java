package GUI;

import javax.swing.*;
import java.util.List;

public class Constants {
    public static final String[] ANCHORS = {
            "Create Paper",
            "Import Paper",
            "View Papers"
    };

    public static final int CREATE_PAPER = 0;
    public static final int IMPORT_PAPER = 1;
    public static final int VIEW_PAPERS  = 2;

    public static ComboBoxModel<String> getAnchorModel() {
        final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addAll(List.of(Constants.ANCHORS));

        return model;
    }
}
