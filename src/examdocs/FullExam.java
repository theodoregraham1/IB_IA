package examdocs;

import database.PaperDatabase;
import utils.Constants;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class FullExam {
    private final ExamPaper paper;
    private final MarkScheme scheme;

    public FullExam(File superDir) {
        this(new File(superDir, Constants.PAPER_DIR_NAME), new File(superDir, Constants.SCHEME_DIR_NAME));
    }

    public FullExam(File paperDir, File schemeDir) {
        this.paper = new ExamPaper(paperDir);
        this.scheme = new MarkScheme(schemeDir);
    }

    public FullExam(File superDir, File oldPaper, File oldScheme) {
        File paperDirectory = new File(superDir, Constants.PAPER_DIR_NAME);
        File schemeDirectory = new File(superDir, Constants.SCHEME_DIR_NAME);

        PaperDatabase.makeDatabase(paperDirectory, oldPaper);
        PaperDatabase.makeDatabase(schemeDirectory, oldScheme);

        this.paper = new ExamPaper(paperDirectory);
        this.scheme = new MarkScheme(schemeDirectory);
    }

    public MarkScheme getScheme() {
        return scheme;
    }

    public ExamPaper getPaper() {
        return paper;
    }

    public int numQuestions() {
        return paper.numQuestions();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullExam fullExam)) return false;
        return Objects.equals(paper, fullExam.getPaper());
    }

    @Override
    public String toString() {
        return paper.toString();
    }
}
