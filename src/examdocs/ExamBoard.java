package examdocs;

import static utils.Constants.*;

import database.PaperDatabase;
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

    private final File directory;
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
     * @param directory the path to the root folder of this exam board
     */
    public ExamBoard(BoardLevel level, File directory) {
        this.level = level;
        this.directory = directory;

        this.infoFile = new File(directory, INFO_FILE_NAME);

        makePapers();
    }

    /**
     * Updates the exam papers list with the data from the input file. File has first line as the directory
     * path and every other as the directory of the exam paper
     */
    public void makePapers() {
        try {
            // Get all the papers from the data file
            String[] lines = FileHandler.readLines(infoFile);

            papers = new ArrayList<>();
            for(String line: lines) {
                papers.add(new ExamPaper(new File(directory, line)));
            }

        } catch (FileNotFoundException e) {
            // If the file doesn't exist, make it ready for papers to be added
            boolean ignored = FileHandler.makeFile(infoFile);
            papers = new ArrayList<>();

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
        // Make the files
        PaperDatabase.makeDatabase(new File(directory, name), document);
        ExamPaper paper = new ExamPaper(new File(document, name));

        paper.makeQuestions();

        papers.add(paper);

        // Check if the paper is already in the info file
        if (!FileHandler.contains(name, infoFile)) {
            return FileHandler.addLine(name + "\n", infoFile);
        }
        return true;
    }

    public boolean addPaper(ArrayList<Question> questions, String name) {
        ExamPaper paper = new ExamPaper(
                questions,
                new File(directory, name)
        );

        papers.add(paper);

        // Check if the paper is already in the info file
        if (!FileHandler.contains(name, infoFile)) {
            return FileHandler.addLine(name + "\n", infoFile);
        }
        return true;
    }

    public ExamPaper getPaper(int index) {
        if (index >= papers.size()) {
            return null;
        }
        return papers.get(index);
    }

    @Override
    public Iterator<Question> iterator() {
        return new Iterator<>() {
            int paperNum = 0;
            int questionIndex = 0;

            @Override
            public boolean hasNext() {
                // Check if there is another question in the current paper
                if (getPaper(paperNum).getQuestion(questionIndex) != null) {
                    return true;
                } else if (getPaper(paperNum+1) != null) {
                    // Check if the next paper exists and if it has any questions
                    paperNum++;
                    return getPaper(paperNum).getQuestion(0) != null;
                }
                return false;
            }

            @Override
            public Question next() {
                questionIndex++;
                return getPaper(paperNum).getQuestion(questionIndex - 1);
            }
        };
    }
}
