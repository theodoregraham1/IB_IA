package GUI;

import examdocs.BoardLevel;
import examdocs.ExamBoard;
import examdocs.ExamPaper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GuiMain {
    private static ExamBoard board = new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE"));

    public static void main(String[] args) {
        // TODO: Loading screen
        board.removePaper("Paper-2022-June-1");
        ExamPaper paper = board.addPaper(new File("./Papers/GCSE/June-2022/Question-paper/Questionpaper-Paper1-June2022.pdf"), "Paper-2022-June-1");
        new SplitPaperPage(paper, new AnchorListener());
    }

    public static class AnchorListener implements ActionListener {
        // This is awful I know, I'm sorry
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source instanceof JComboBox<?>) {
                Object selectedItem = ((JComboBox<?>) source).getSelectedItem();
                getPage(selectedItem);
            }
        }
    }

    public static JFrame getPage(Object selectedAnchor) {
        if (!(selectedAnchor instanceof String)) {
            return null;
        }
        int index = linearSearch(Constants.ANCHORS, selectedAnchor);

        return switch (index) {
            case 0 -> new CreatePaperPage(board);
            case 1 -> new ImportPaperPage();
            case 2 -> null; //TODO
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
