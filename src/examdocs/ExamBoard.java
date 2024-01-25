package examdocs;

import static utils.Constants.*;
import utils.FileHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamBoard {
    private static final Logger logger = Logger.getLogger(ExamBoard.class.getName());
    private static final String INFO_FILE_NAME = "board_information.txt";

    private String dirPath;
    private File infoFile;
    private String name;
    private ArrayList<ExamPaper> papers;

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    /**
     * Makes a new instance of Exam Board and pulls its papers from the text file
     * @param name - the name of this exam board
     * @param dirPath - the path to the root folder of this exam board
     */
    public ExamBoard(String name, String dirPath) {
        this.name = name;
        this.dirPath = dirPath;
        this.infoFile = new File(dirPath + INFO_FILE_NAME);

        makePapers(infoFile);
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
                                PAPER_FILE_NAME,
                                PAPER_DIR_FORMAT.formatted(dirPath, lines[i])));
            }

        } catch (FileNotFoundException e) {
            boolean ignored = FileHandler.makeFile(examPapersFile);

        } catch (IOException e) {
            // TODO: More robust error handling
            logger.log(Level.SEVERE, e.toString());
        }
    }

    /**
     * Adds a new paper from scratch, assuming nothing has been saved before.
     * Then saves images and questions
     * @param document the pdf file to make the paper from
     * @param name the name of the paper, in the format: YEAR/MONTH/NUMBER
     */
    public void addPaper(File document, String name) {
        ExamPaper paper = new ExamPaper(
                PAPER_FILE_NAME,
                PAPER_DIR_FORMAT.formatted(dirPath, name));

        try {
            Files.copy(document.toPath(), paper.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            paper.saveAsImages();

            paper.makeQuestions();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to add paper due to IOException");
            return;
        }
        papers.add(paper);

        FileHandler.addLine(name, infoFile);
    }
}
