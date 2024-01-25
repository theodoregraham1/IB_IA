package examdocs;

import static utils.Constants.*;
import utils.FileHandler;

import static java.io.File.separatorChar;
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
        this.infoFile = new File(dirPath + separatorChar + INFO_FILE_NAME);

        makePapers();
    }

    /**
     * Updates the exam papers list with the data from the input file. File has first line as the directory
     * path and every other as the directory of the exam paper
     */
    public void makePapers() {
        try {
            String[] lines = FileHandler.readLines(infoFile);

            papers = new ArrayList<>();
            for (int i = 1; i < lines.length - 1; i++) {
                papers.add(new ExamPaper(
                                PAPER_FILE_NAME,
                                PAPER_DIR_FORMAT.formatted(dirPath, lines[i])));
            }

        } catch (FileNotFoundException e) {
            boolean ignored = FileHandler.makeFile(infoFile);

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
        String paperDirPath = PAPER_DIR_FORMAT.formatted(dirPath, name);

        try {
            FileHandler.makeFile()
            File newPaperFile = new File(paperDirPath + separatorChar + PAPER_FILE_NAME);
            boolean success = newPaperFile.createNewFile();

            if (!success) {
                throw new IOException("Could not make new file for paper: " + newPaperFile.getPath());
            }

            Files.copy(document.toPath(), newPaperFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to add paper due to IOException: " + e);
            return;
        }

        ExamPaper paper = new ExamPaper(
                PAPER_FILE_NAME,
                PAPER_DIR_FORMAT.formatted(dirPath, name));

        paper.saveAsImages();
        paper.makeQuestions();

        papers.add(paper);

        FileHandler.addLine(name, infoFile);
    }
}