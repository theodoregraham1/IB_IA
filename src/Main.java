import database.ImageFile;
import examdocs.*;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ExamBoard board = new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE"));
        board.getPaper(0).makeQuestions();
    }
}