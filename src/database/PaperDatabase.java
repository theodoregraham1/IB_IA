package database;

import examdocs.Document;
import examdocs.Page;
import examdocs.Question;
import utils.FileHandler;
import utils.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static utils.Constants.*;


public class PaperDatabase extends Database {
    public final PageTable pageTable;
    public final QuestionTable questionTable;

    public PaperDatabase(File directory, Document document) {
        this.pageTable = new PageTable(new File(directory, PAGES_DIR_NAME), document);
        this.questionTable = new QuestionTable(new File(directory, QUESTIONS_DIR_NAME));
    }

    /**
     * Creates a completely new paper in its entirety, for use by paper compilation front-end
     * @param directory the root of the database system
     * @param pages images of every single page in order
     * @param questions the questions used to compile the new paper
     * @param questionData data for each question in the form: [startPage, startPercent, endPage, endPercent]
     */
    public PaperDatabase(File directory, Image[] pages, Question[] questions, int[][] questionData) {
        this.pageTable = new PageTable(new File(directory, PAGES_DIR_NAME), pages);
        this.questionTable = new QuestionTable(new File(directory, QUESTIONS_DIR_NAME), questions, questionData);
    }

    public class QuestionTable
            extends ImageTable<Question> {

        public QuestionTable(File imageDir) {
            super(imageDir);
        }

        public QuestionTable(File imageDir, Question[] questions, int[][] questionData) {
            super(imageDir);

            for (int i=0; i<questions.length; i++) {
                setRow(
                        questions[i].getImage(),
                        new int[] {
                                i,
                                questionData[i][0],
                                questionData[i][1],
                                questionData[i][2],
                                questionData[i][3],
                                questions[i].getMarks()
                        }
                );
            }
        }

        public void setRow(int[] data) {
            if (data.length != getDataLength()) {
                return;
            }
            super.setRow(Question.createQuestionImage(
                            pageTable.getRows(data[1], data[3]+1).toArray(new Page[0]), // This may cause issues in the future, but +1 was added for selection in the GUI
                            data[2],
                            data[4]
                    ),
                    data
            );
        }

        private int getMarksFromName(String name) {
            return Integer.parseInt(name.substring(name.length() - 7, name.length() - 4));
        }

        /**
         * Returns the length of each piece of data in the RandomAccessFile
         * @return the number of bytes per ImageFile
         */
        @Override
        protected int getDataLength() {
            // questionNumber,
            // startPage,
            // startPercent,
            // endPage,
            // endPercent,
            // marks

            return 6;
        }

        @Override
        protected Question getObjectInstance(File file) {
            return new Question(file, this.getMarksFromName(file.getName()), logger);
        }

        @Override
        protected File getInstanceFile(int index, int extraData) {
            if (extraData != -1) {
                return new File(QUESTION_FILE_FORMAT.formatted(imageDir.getPath(), index, extraData));
            }

            String nameToCheckAgainst = CONSTRAINED_QUESTION_FORMAT.formatted(index);
            File[] allFiles = imageDir.listFiles(pathname -> pathname.getName().contains(nameToCheckAgainst));

            if (allFiles == null || allFiles.length == 0) return null;
            else return allFiles[0];
        }

        @Override
        protected Question generateObjectInstance(RandomAccessFile rf, int index) throws IOException {
            // Get page information from the file and build the question from that

            // Get page information
            int startPage = rf.read();
            int startPercent = rf.read();

            int endPage = rf.read();
            int endPercent = rf.read();

            int marks = rf.read();

            // Make the question
            Page[] pages = pageTable.getRows(startPage, endPage).toArray(new Page[0]);

            BufferedImage image = Question.createQuestionImage(pages, startPercent, endPercent);

            return saveImage(image, index, marks);
        }
    }

    public class PageTable
        extends ImageTable<Page> {

        private final Document document;

        public PageTable(File imageDir, Document document) {
            super(imageDir);

            this.document = document;
            makeFromDocument();
        }

        public PageTable(File imageDir, Image[] pageImages) {
            super(imageDir);

            for (int i=0; i < pageImages.length; i++) {
                setRow(ImageHandler.copyImage(pageImages[i]), new int[] {i});
            }

            this.document = new Document(
                    getRows(0, -1).toArray(new Page[0]),
                    new File(imageDir.getParentFile(), PAPER_FILE_NAME)
            );
        }

        public void makeFromDocument() {
            // Assume document is not too large where images will overflow memory

            try (RandomAccessFile rf = new RandomAccessFile(dataFile, "r")) {
                // Check if the document has already been saved
                if (((long) document.length() * getDataLength()) <= rf.length()
                        && Objects.requireNonNull(dataFile.getParentFile().list()).length-1 == document.length()) {
                    return;
                }
                rf.close();

                // Destroy and recreate the data file
                if (!dataFile.delete()) {
                    return;
                }
                if (!dataFile.createNewFile()) {
                    return;
                }
            } catch (FileNotFoundException e) {
                FileHandler.clearDirectory(dataFile.getParent());
                FileHandler.makeFile(dataFile);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                return;
            }

            // Write the images
            int index = 0;
            for (BufferedImage image: document.splitToImages()) {
                setRow(image, new int[]{index});
                index ++;
            }
        }

        public Document getDocument() {
            return document;
        }

        /**
         * Returns the length of each piece of data in the RandomAccessFile
         * @return the number of bytes per ImageFile
         */
        @Override
        protected int getDataLength() {
            // pageNumber
            return 1;
        }

        @Override
        protected Page getObjectInstance(File file) {
            return new Page(file, logger);
        }

        @Override
        protected File getInstanceFile(int index, int ignored) {
            return new File(PAGE_FILE_FORMAT.formatted(imageDir.getPath(), index));
        }

        @Override
        protected Page generateObjectInstance(RandomAccessFile rf, int index) {
            pageTable.makeFromDocument();

            return pageTable.getRows(index, index+1).get(0);
        }
    }

    public static void makeDatabase(File directory, File newPaper) {
        // Save the paper
        File paperFile = new File(directory, PAPER_FILE_NAME);

        boolean replace = false;
        if (paperFile.exists()) {
            if (!newPaper.equals(paperFile)) {
                // If the current file is not the same as the new one (based on abstract paths), replace it
                replace = true;
            }
        } else {
            replace = true;
        }

        if (replace) {
            try {
                boolean ignored = paperFile.mkdirs();
                Files.copy(newPaper.toPath(), paperFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
