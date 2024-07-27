package GUI;

import examdocs.BoardLevel;
import examdocs.ExamBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static utils.Searches.linearSearch;

public class AnchorListener implements ActionListener {
    // This is awful I know, I'm sorry

    // TODO: Have this chosen on start-up by user
    private final ExamBoard board = new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE"));

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source instanceof JComboBox<?>) {
            Object selectedItem = ((JComboBox<?>) source).getSelectedItem();
            getPage(selectedItem);

            for (Container c = (JComboBox<?>) source; c != null; c=c.getParent()) {
                if (c instanceof JFrame) {
                    c.setVisible(false);
                    ((JFrame) c).dispose();
                }
            }
        }
    }

    public JFrame getPage(Object selectedAnchor) {
        if (!(selectedAnchor instanceof String || selectedAnchor instanceof Integer)) {
            return null;
        } else if (selectedAnchor instanceof String) {
            selectedAnchor = linearSearch(Constants.ANCHORS, selectedAnchor);
        }

        // Move indexes to constants
        return switch ((int) selectedAnchor) {
            case Constants.CREATE_PAPER -> new CreatePaperPage(board, new AnchorListener());
            case Constants.IMPORT_PAPER -> new ImportPaperPage(board, new AnchorListener());
            case Constants.VIEW_PAPERS -> new ViewPapersPage(board, new AnchorListener());
            default -> null;
        };
    }
}