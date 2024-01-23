package examdocs;

import utils.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamBoard {
    private static final Logger logger = Logger.getLogger(ExamPaper.class.getName());

    private String dirPath
    private String name;
    private ArrayList<ExamPaper> papers;

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    public ExamBoard(String name, File examPapersFile) {
        this.name = name;

        makePapers(examPapersFile);
    }

    /**
     * Updates the exam papers list with the data from the input file. File has first line as the directory
     * path and every other as the directory of the exam paper
     * @param examPapersFile
     */
    public void makePapers(File examPapersFile) {
        String[] lines = FileHandler.readLines(examPapersFile);

        this.dirPath = lines[0];

        papers = new ArrayList<>();
        for (int i=1; i<lines.length-1; i++) {
            papers.add(new ExamPaper(Constants., ))
        }
    }
}
