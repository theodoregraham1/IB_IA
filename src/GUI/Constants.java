package GUI;

import javax.swing.*;
import java.util.List;

public class Constants {
    public static final String[] ANCHORS = {
            "Create Paper",
            "Import Paper",
            "View Papers"
    };

    public static ComboBoxModel<String> getAnchorModel() {
        final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addAll(List.of(Constants.ANCHORS));

        return model;
    }
}
