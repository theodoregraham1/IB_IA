package examdocs;

import database.PaperDatabase;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;

import static utils.Constants.PAPER_FILE_NAME;

public abstract class QuestionPaper
        implements Iterable<Page> {
    protected final Document document;
    protected final PaperDatabase database;

    protected QuestionPaper(File databaseFile) {
        this.document = new Document(new File(databaseFile, PAPER_FILE_NAME));
        this.database = new PaperDatabase(databaseFile, document);
    }

    protected QuestionPaper(File databaseFile, Image[] pages, Question[] questions, int[][] questionData) {
        this.database = new PaperDatabase(databaseFile, pages, questions, questionData);
        this.document = database.pageTable.getDocument();
    }

    protected QuestionPaper(File databaseFile, Question[] questions) {
        // Place each question on a page until the next one won't fit, then continue

        PaperGenerator autoGenerator = new PaperGenerator(questions);

        database = new PaperDatabase(databaseFile, autoGenerator.getPages(), questions, autoGenerator.getQuestionsData());
        document = database.pageTable.getDocument();
    }

    public void saveQuestion(int questionNumber, int startPage, int startPercent, int endPage, int endPercent, int mark) {
        database.questionTable.setRow(new int[]{
                questionNumber,
                startPage,
                startPercent,
                endPage,
                endPercent,
                mark
        });
    }

    /**
     * Get specific question from the paper
     *
     * @param index the number for the question
     * @return a reference to the Question
     */
    public Question getQuestion(int index) {
        try {
            return database.questionTable.getRows(index, index + 1).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void clearQuestions() {
        database.questionTable.clear();
    }

    public void removeQuestion(int index) {
        database.questionTable.deleteRow(index);
    }

    public Page getPage(int index) {
        try {
            return database.pageTable.getRows(index, index + 1).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int length() {
        return database.pageTable.length();
    }

    public int numQuestions() {
        return database.questionTable.length();
    }

    public int totalMarks() {
        int total = 0;

        Question q;
        for (int i=0; i<numQuestions(); i++) {
            q = getQuestion(i);

            if (q != null) {
                total += q.getMarks();
            }
        }
        return total;
    }

    public File getDocumentFile() {
        return document.getFile();
    }

    @Override
    public Iterator<Page> iterator() {
        return new Iterator<>() {
            int count = 0;

            @Override
            public boolean hasNext() {
                return !database.pageTable.getRows(count, count + 1).isEmpty();
            }

            @Override
            public Page next() {
                count++;
                return database.pageTable.getRows(count - 1, count).get(0);
            }
        };
    }

    @Override
    public boolean equals(Object paper) {
        if (paper instanceof ExamPaper) {
            return getDocumentFile().equals(((ExamPaper) paper).getDocumentFile());
        }
        return false;
    }

    @Override
    public String toString() {
        Path path = getDocumentFile().toPath();
        int nameCount = path.getNameCount();

        return path.getName(nameCount-3).toString();
    }
}
