import examdocs.BoardLevel;
import examdocs.ExamBoard;
import examdocs.ExamPaper;
import examdocs.Question;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ExamBoard board = new ExamBoard(BoardLevel.GCSE, "./Papers/GCSE");

        ArrayList<Question> questions = new ArrayList<>();

        for (Question q: board) {
            questions.add(q);
        }

        board.addPaper(questions, "NewPaper1");
    }
}