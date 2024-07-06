package GUI;

import examdocs.BoardLevel;
import examdocs.ExamBoard;
import examdocs.ExamPaper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GuiMain {
    private static ExamBoard board = new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE"));

    public static void main(String[] args) {
        try {
            // Set the look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getPage(Constants.CREATE_PAPER);
    }

    public static class AnchorListener implements ActionListener {
        // This is awful I know, I'm sorry
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
    }

    public static JFrame getPage(Object selectedAnchor) {
        if (!(selectedAnchor instanceof String || selectedAnchor instanceof Integer)) {
            return null;
        } else if (selectedAnchor instanceof String) {
            selectedAnchor = linearSearch(Constants.ANCHORS, selectedAnchor);
        }

        // Move indexes to constants
        return switch ((int) selectedAnchor) {
            case Constants.CREATE_PAPER -> new CreatePaperPage(board, new AnchorListener());
            case Constants.IMPORT_PAPER -> new ImportPaperPage(board, new AnchorListener());
            case Constants.VIEW_PAPERS -> null; //TODO
            default -> null;
        };
    }

    public static int linearSearch(Object[] array, Object o) {
        for (int i=0; i<array.length; i++) {
            if (array[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }
}
