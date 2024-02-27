import examdocs.*;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ExamBoard board = new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE"));

        ArrayList<Question> questions = new ArrayList<>();
        for (Question q: board) {
            System.out.println(q.getFile());
            questions.add(q);
        }
        board.addPaper(questions, "myPaper1");
    }
}