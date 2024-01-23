package examdocs;

import utils.Constants;
import utils.FileHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamBoard {
    private static final Logger logger = Logger.getLogger(ExamBoard.class.getName());
    private static final String INFO_FILE = "board_information.txt";

    private String dirPath;
    private String name;
    private ArrayList<ExamPaper> papers;

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    /**
     * Makes a new instance of Exam Board and pulls its papers from the text file
     * @param name the name of this exam board
     * @param dirPath the path to the root folder of this exam board
     */
    public ExamBoard(String name, String dirPath) {
        this.name = name;
        this.dirPath = dirPath;

        makePapers(new File(dirPath + INFO_FILE));
    }

    /**
     * Updates the exam papers list with the data from the input file. File has first line as the directory
     * path and every other as the directory of the exam paper
     * @param examPapersFile the file where the exam board information is stored
     */
    public void makePapers(File examPapersFile) {
        try {
            String[] lines = FileHandler.readLines(examPapersFile);

            this.dirPath = lines[0];

            papers = new ArrayList<>();
            for (int i = 1; i < lines.length - 1; i++) {
                papers.add(new ExamPaper(
                                Constants.PAPER_FILE_NAME,
                                Constants.PAPER_DIR_FORMAT.formatted(dirPath, lines[i])));
            }

        } catch (FileNotFoundException e) {
            boolean ignored = FileHandler.makeFile(examPapersFile);

        } catch (IOException e) {
            // TODO: More robust error handling
            logger.log(Level.SEVERE, e.toString());
        }
    }

    public void addPaper() {

    }
}
