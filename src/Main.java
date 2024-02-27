import database.ImageFile;
import examdocs.*;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ExamBoard board = new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE"));

        ArrayList<ImageFile> questions = new ArrayList<>();
        for (Page q: board.getPaper(0)) {
            System.out.println(q.getFile());
            questions.add(q);
        }
        board.addPaper(questions, "myPaper1");
    }
}