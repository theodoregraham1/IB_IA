import examdocs.BoardLevel;
import examdocs.ExamBoard;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        ExamBoard board = new ExamBoard(BoardLevel.GCSE, new File("./Papers/GCSE"));
        board.getPaper(0);
    }
}