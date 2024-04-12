import database.ImageFile;
import examdocs.*;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ExamBoard board = new ExamBoard(BoardLevel.IB, new File("./Papers/IB"));

        board.addPaper(new File("Papers/may-23-CompSci-HL-P1.pdf"), "may23-P1");
    }
}