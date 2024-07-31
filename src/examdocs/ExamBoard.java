package examdocs;

import utils.Constants;
import utils.FileHandler;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamBoard
        implements Iterable<Question> {
    private static final Logger logger = Logger.getLogger(ExamBoard.class.getName());
    private static final String INFO_FILE_NAME = "board_information.txt";

    private final File directory;
    private final File infoFile;

    private final BoardLevel level;
    private ArrayList<FullExam> exams;

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

    public ExamBoard(BoardLevel level) {
        this.level = level;
        this.directory = new File(Constants.BOARD_DIR_FORMAT.formatted(level.toString()));

        this.infoFile = new File(directory, INFO_FILE_NAME);
        makePapers();
    }

    /**
     * Updates the exam papers list with the data from the input file. File has first line as the directory
     * path and every other as the directory of the exam paper
     */
    public void makePapers() {
        exams = new ArrayList<>();

        try {
            // Get all the papers from the data file
            String[] lines = FileHandler.readLines(infoFile);


            for (String line: lines) {
                File examDirectory = new File(directory, line);
                if (examDirectory.exists() && !examDirectory.equals(directory)) {
                    exams.add(new FullExam(examDirectory));
                }
            }

        } catch (FileNotFoundException e) {
            // If the file doesn't exist, make it ready for papers to be added
            boolean ignored = FileHandler.makeFile(infoFile);

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
    }

    /**
     * Adds a new paper from scratch, assuming nothing has been saved before.
     * Then saves images and questions
     *
     * @param paperDocument the PDF file to make the paper from
     * @param schemeDocument the PDF file to make the mark scheme from
     * @param name the name of the paper, in the format: YEAR-MONTH-NUMBER
     */
    public FullExam addPaper(File paperDocument, File schemeDocument, String name) {
        // Make the files
        File superDirectory = new File(directory, name);

        FullExam newExam = new FullExam(superDirectory, paperDocument, schemeDocument);

        // Check if the paper is already in the info file
        return addExamToFile(name, newExam);
    }

    public FullExam addPaper(String name, Image[] paperPages, Question[] questions, int[][] paperQuestionData) {
        File superDirectory = new File(directory, name);

        FullExam newExam = new FullExam(superDirectory, paperPages, questions, paperQuestionData);

        // Check if the paper is already in the info file
        return addExamToFile(name, newExam);
    }

    private FullExam addExamToFile(String name, FullExam newExam) {
        if (!FileHandler.contains(name, infoFile)) {
            FileHandler.addLine("\n"+name, infoFile);

            exams.add(newExam);
        } else {
            // Replace old version of the paper
            int i = exams.indexOf(newExam);

            if (i != -1) {
                exams.set(i, newExam);
            } else {
                exams.add(newExam);
            }
        }

        return newExam;
    }

    public FullExam getExam(int index) {
        if (index >= exams.size()) {
            return null;
        }
        return exams.get(index);
    }

    public ExamPaper getPaper(int index) {
        if (getExam(index) == null) {
            return null;
        }
        return getExam(index).getPaper();
    }

    public void removeExam(String name) {
        removeExam(new File(directory, name));
    }

    public void removeExam(File examDirectory) {
        if (!examDirectory.getParentFile().equals(directory)) {
            return;
        }
        FileHandler.clearDirectory(examDirectory);
        FileHandler.removeLine(examDirectory.getName(), infoFile);
        examDirectory.delete();
    }

    public int size() {
        return exams.size();
    }

    @Override
    public Iterator<Question> iterator() {
        return new Iterator<>() {
            private int paperNum = 0;
            private int questionIndex = 0;

            @Override
            public boolean hasNext() {
                // Check if there is another question in the current paper
                ExamPaper paper = getPaper(paperNum);
                if (paper == null) {
                    return false;
                } else if (paper.getQuestion(questionIndex+1) != null) {
                    questionIndex++;
                    return true;

                }
                paper = getPaper(paperNum+1);
                if (paper != null) {
                    // Check if the next paper exists and if it has any questions
                    paperNum++;
                    questionIndex = 0;

                    return paper.getQuestion(questionIndex) != null;
                }
                return false;
            }

            @Override
            public Question next() {
                return getPaper(paperNum).getQuestion(questionIndex);
            }
        };
    }
}
