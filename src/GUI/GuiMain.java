package GUI;

import examdocs.BoardLevel;
import examdocs.ExamBoard;
import examdocs.ExamPaper;

import java.io.File;

public class GuiMain {
    public static void main(String[] args) {
        // TODO: Loading screen
        ExamBoard gcseBoard = new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE"));
        ExamPaper paper = gcseBoard.addPaper(new File("./Papers/GCSE/June-2022/Question-paper/Questionpaper-Paper1-June2022.pdf"), "Paper-2022-June-1");
        SplitPaperPage splitter = new SplitPaperPage(paper);
    }
}
