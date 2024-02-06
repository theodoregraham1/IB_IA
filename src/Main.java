import examdocs.BoardLevel;
import examdocs.ExamBoard;
import examdocs.Question;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ExamBoard board = new ExamBoard(BoardLevel.GCSE, "./Papers/GCSE");
        board.addPaper(
                new File("./Papers/GCSE/June-2013/Question-paper/Questionpaper-Paper1-June2017.pdf"),
                "Paper-2013-June-1"
        );

        /*
        ArrayList<Question> questions = new ArrayList<>();

        for (Question q: board) {
            questions.add(q);
        }

        board.addPaper(questions, "NewPaper1");

         */
    }
}