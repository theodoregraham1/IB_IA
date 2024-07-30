package examdocs;

import database.PaperDatabase;
import utils.Constants;

import java.awt.*;
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

    public FullExam(File superDir, Image[] paperPages, Question[] paperQuestions, int[][] paperQuestionData) {
        this.paper = new ExamPaper(new File(superDir, Constants.PAPER_DIR_NAME), paperPages, paperQuestions, paperQuestionData);

        // Get all the questions for the mark scheme
        Question[] schemeQuestions = new Question[paperQuestions.length];
        for (int i=0; i<schemeQuestions.length; i++) {
            schemeQuestions[i] = paperQuestions[i].getMarkSchemeQuestion();
        }

        this.scheme = new MarkScheme(new File(superDir, Constants.SCHEME_DIR_NAME), schemeQuestions);
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
