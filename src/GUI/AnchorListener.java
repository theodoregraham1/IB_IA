package GUI;

import examdocs.BoardLevel;
import examdocs.ExamBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static utils.Searches.linearSearch;

public class AnchorListener implements ActionListener {
    private final ExamBoard board;

    public AnchorListener(BoardLevel level) {
        board = new ExamBoard(level);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source instanceof JComboBox<?> comboBox) {
            Object selectedItem = comboBox.getSelectedItem();
            getPage(selectedItem);

            for (Container c = comboBox; c != null; c=c.getParent()) {
                if (c instanceof JFrame frame) {
                    c.setVisible(false);
                    frame.dispose();
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
            case Constants.CREATE_PAPER -> new CreatePaperPage(board, this);
            case Constants.IMPORT_PAPER -> new ImportPaperPage(board, this);
            case Constants.VIEW_PAPERS -> new ViewPapersPage(board, this);
            default -> null;
        };
    }
}