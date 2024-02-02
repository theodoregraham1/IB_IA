package examdocs;

import static utils.Constants.*;
import utils.FileHandler;

import static java.io.File.separatorChar;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Be able to join questions into a big paper
public class ExamBoard
        implements Iterable<Question> {
    private static final Logger logger = Logger.getLogger(ExamBoard.class.getName());
    private static final String INFO_FILE_NAME = "board_information.txt";

    private final String dirPath;
    private final File infoFile;
    private final BoardLevel level;
    private ArrayList<ExamPaper> papers;


    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    /**
     * Makes a new instance of Exam Board and pulls its papers from the text file
     * @param level the level of this exam board
     * @param dirPath the path to the root folder of this exam board
     */
    public ExamBoard(BoardLevel level, String dirPath) {
        this.level = level;
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
     * @param name the name of the paper, in the format: YEAR-MONTH-NUMBER
     */
    public boolean addPaper(File document, String name) {
        String paperDirPath = PAPER_DIR_FORMAT.formatted(dirPath, name);

        try {
            // Make required directories
            if (!FileHandler.clearDirectory(paperDirPath)) {
                return false;
            }
            // Copy the old paper into the new file
            File newPaperFile = new File(paperDirPath + PAPER_FILE_NAME);
            Files.copy(document.toPath(), newPaperFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


        } catch (IOException e) {
            // TODO: Better file handling here
            logger.log(Level.SEVERE, "Unable to add paper due to IOException: " + e);
            return false;
        }

        ExamPaper paper = new ExamPaper(
                PAPER_FILE_NAME,
                PAPER_DIR_FORMAT.formatted(dirPath, name));

        paper.makeQuestions();

        papers.add(paper);

        // Check if the paper is already in the info file
        if (!FileHandler.contains(name, infoFile)) {
            return FileHandler.addLine(name + "\n", infoFile);
        }
        return true;
    }

    public void addPaper(ArrayList<Question> questions, String name) {
        papers.add(new ExamPaper(
                questions,
                PAPER_FILE_NAME,
                PAPER_DIR_FORMAT.formatted(dirPath, name)
        ));
    }

    @Override
    public Iterator<Question> iterator() {
        return new Iterator<Question>() {
            int paperNum = 0;
            int questionIndex = 0;
            @Override
            public boolean hasNext() {
                if (papers.get(paperNum).getQuestion(questionIndex) != null) {
                    return true;
                } else if (papers.get(paperNum+1) != null) {
                    return papers.get(paperNum + 1).getQuestion(0) != null;
                }
                return false;
            }

            @Override
            public Question next() {
                questionIndex ++;
                return papers.get(paperNum).getQuestion(questionIndex-1);
            }
        };
    }
}
