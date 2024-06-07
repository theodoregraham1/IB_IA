package GUI;

import examdocs.BoardLevel;
import examdocs.ExamBoard;

import java.io.File;

public class GuiMain {
    public static void main(String[] args) {
        // CreatePaperPage frame = new CreatePaperPage(new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE")));
        SplitPaperPage frame = new SplitPaperPage(new File("./Papers/GCSE/June-2022/Question-paper/Questionpaper-Paper1-June2022.pdf"));
    }
}
