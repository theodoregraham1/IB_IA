package examdocs;

import database.PaperDatabase;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import utils.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
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

        ArrayList<BufferedImage> pages = new ArrayList<>();

        int[][] questionsData = autoGeneratePaper(questions, pages);

        database = new PaperDatabase(databaseFile, pages.toArray(new Image[0]), questions, questionsData);
        document = database.pageTable.getDocument();
    }

    private int[][] autoGeneratePaper(Question[] questions, ArrayList<BufferedImage> pages) {
        int[][] questionsData = new int[questions.length][4];

        int currentHeight = 0;
        int pageNum = 0;

        pages.add(pageNum, getNewPageImage());

        for (int i = 0; i < questions.length; i++) {
            BufferedImage currentImage = ImageHandler.copyImage(questions[i].getImage()
                    .getScaledInstance(pages.get(pageNum).getWidth(), -1, Image.SCALE_SMOOTH));

            int newHeight = currentImage.getHeight();

            Graphics g = pages.get(pageNum).getGraphics();

            if (currentHeight + newHeight > pages.get(pageNum).getHeight()) {
                pageNum ++;
                pages.add(pageNum, getNewPageImage());
                currentHeight = 0;
            }

            int startPage = pageNum;
            int startHeight = currentHeight;

            while (newHeight > pages.get(pageNum).getHeight()) {
                // Draw question if it is bigger than a page, this does not guarantee a good split, but it's good enough
                // and almost always will get a good split

                currentImage = ImageHandler.copyImage(
                        currentImage.getScaledInstance(pages.get(pageNum).getWidth(), -1, Image.SCALE_SMOOTH)
                );

                // Draw first page
                g.drawImage(
                        currentImage.getSubimage(0, 0, currentImage.getWidth(), pages.get(pageNum).getHeight()),
                        0,
                        0,
                        null
                );

                pageNum++;
                pages.add(pageNum, getNewPageImage());
                currentHeight = 0;

                currentImage = currentImage.getSubimage(
                        0,
                        pages.get(pageNum).getHeight(),
                        currentImage.getWidth(),
                        currentImage.getHeight() - pages.get(pageNum).getHeight()
                );

                newHeight = currentImage.getHeight();
            }

            g.drawImage(
                    currentImage,
                    0,
                    currentHeight,
                    pages.get(pageNum).getWidth(),
                    newHeight,
                    null
            );

            currentHeight += newHeight;

            questionsData[i] = new int[] {
                    startPage,
                    ImageHandler.heightToPercentage(pages.get(pageNum), startHeight),
                    pageNum,
                    ImageHandler.heightToPercentage(pages.get(pageNum), currentHeight)
            };
        }

        return questionsData;
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
        for (int i=0; i< numQuestions(); i++) {
            q = getQuestion(i);

            total += q.getMarks();
        }
        return total;
    }

    public File getDocumentFile() {
        return document.getFile();
    }

    private BufferedImage getNewPageImage() {
        return new BufferedImage((int) PDRectangle.A4.getWidth(), (int) PDRectangle.A4.getHeight(), BufferedImage.TYPE_INT_RGB);
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
