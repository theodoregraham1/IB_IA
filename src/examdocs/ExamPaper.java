package examdocs;


import java.awt.*;
import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;


public class ExamPaper extends QuestionPaper {
    private static final Logger logger = Logger.getLogger(ExamPaper.class.getName());

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    /**
     * Creates a new exam paper and checks whether its images have been saved, but does not save them
     * @param databaseFile the directory where this paper has its database's root
     */
    public ExamPaper(File databaseFile) {
        super(databaseFile);
    }

    public ExamPaper(File databaseFile, Image[] pages, Question[] questions, int[][] questionData) {
        super(databaseFile, pages, questions, questionData);
    }
}
