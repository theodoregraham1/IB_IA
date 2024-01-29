import examdocs.BoardLevel;
import examdocs.ExamBoard;
import examdocs.ExamPaper;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        ExamBoard board = new ExamBoard(BoardLevel.GCSE, "./Papers/GCSE");
        board.addPaper(
                new File("./Papers/GCSE/June-2013/Question-paper/Questionpaper-Paper1-June2017.pdf"),
                "Paper-2013-June-1"
                );
        /*
        ExamPaper paper = new ExamPaper("Questionpaper-Paper1-June2017.pdf", );
        paper.saveAsImages();
        paper.makeQuestions();
         */
    }
}