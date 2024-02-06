package examdocs;

import commands.Command;
import commands.Commands;
import database.PaperDatabase;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import utils.Constants;
import utils.FileHandler;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamPaper {
    private static final Logger logger = Logger.getLogger(ExamPaper.class.getName());

    // TODO: Move this to database
    private boolean imagesSaved;
    private final Document document;
    private final PaperDatabase database;

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    /**
     * Creates a new exam paper and checks whether its images have been saved, but does not save them
     * @param fileName the name of the file, relative to dirPath
     * @param dirPath the path to the directory where the paper, its questions and its page images are stored. From the root directory
     */
    public ExamPaper(String fileName, String dirPath) {
        this.document = new Document(fileName, dirPath);
        this.database = new PaperDatabase(new File(fileName + File.separator + dirPath));

        this.imagesSaved = document.checkImageDir();
    }

    public ExamPaper(ArrayList<Question> questions, String fileName, String dirPath) {
        FileHandler.clearDirectory(dirPath);

        this.document = new Document(fileName, dirPath);
        this.database = new PaperDatabase(new File(fileName + File.separator + dirPath));

        // TODO: Change this to be based on document interacting with database
        for (Question question: questions) {
            document.addPage(question);
        }
    }

    /**
     * Saves images from exam paper's document, updating imagesSaved if it was successful. Only saves
     * images if they are not already saved
     */
    public void saveAsImages() {
        // TODO: refactor image file processing into the database
        if (!(imagesSaved)) {
            imagesSaved = document.saveAsImages();

            if (imagesSaved) {
                logger.log(Level.INFO, "Saved images from paper to directory");
            }
        }
    }

    /**
     * Saves all the questions in files and updates the instance's questions to match.
     * Based on user input
     */
    public void makeQuestions() {
        Commands commands = new Commands(new Command[] {
                new Command("end", new String[]{"end", "e"}),
                new Command("start", new String[]{"start", "s"}),
                new Command("exit", new String[]{"exit"}),
                new Command("pass", new String[]{"pass", "p", " "})
        });

        // Save page images
        this.saveAsImages();

        Scanner scanner = new Scanner(System.in);

        int pageNumber = 0, startPercent = 0, startPage = 0, questionNumber = 0;
        boolean ended = false, inQuestion = false;

        while (!ended) {
            // Command line interface
            System.out.printf("Page number %d reached\n", pageNumber);

            // Get command
            Command command = null;
            while (command == null) {
                command = commands.scanCommand(scanner);

                // Check that questions are being started/ended at correct times
                if (command.equals("start") && inQuestion) {
                        System.out.println("Question cannot be started until current has been ended");
                        command = null;
                } else if (command.equals("end") && !inQuestion) {
                        System.out.println("Question must be started before being ended");
                        command = null;
                }
            }

            if (command.equals("end") || command.equals("start")) {
                System.out.print("Percentage: ");
                int linePercent = Integer.parseInt(scanner.next());

                if (!inQuestion) {
                    // Start creating new question
                    startPercent = linePercent;
                    startPage = pageNumber;

                } else {
                    // Save the current question
                    database.questionTable.setRow(new int[] {
                            questionNumber,
                            startPage,
                            startPercent,
                            pageNumber,
                            linePercent
                    });

                    questionNumber ++;
                }

                // Allow a question to start on the same page as the previous one ended
                pageNumber --;

                inQuestion = !inQuestion;
            } else if (command.equals("exit")) {
                ended = true;
            }

            // Get the next image
            pageNumber ++;
        }
    }

    /**
     * Save a question to the directory for images
     * @param questionImage the complete image to be saved for the question
     * @param questionNumber the number of the question in the paper
     * @param dirPath the directory where the question should be saved
     * @return the created question, null if there was an error
     */
    public Question saveQuestion(BufferedImage questionImage, int questionNumber, String dirPath) {
        File outputFile = new File(Constants.QUESTION_FILE_FORMAT.formatted(dirPath, questionNumber));

        try {
            ImageIO.write(questionImage,
                    Constants.IMAGE_IO_FORMAT,
                    outputFile);

            logger.log(Level.INFO, "Saved question number %d to file location: %s".formatted(questionNumber, outputFile.getCanonicalPath()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
            return null;
        }

        return new Question(outputFile, logger);
    }

    /**
     * Get specific question from the paper
     * @param index the number for the question
     * @return a reference to the Question
     */
    public Question getQuestion(int index) {
        return (Question) database.questionTable.getRows(index, index+1).get(0);
    }

    /**
     * Returns the pdf file location for this paper
     * @return a File for this paper's file
     */
    public File getFile() {
        return new File(document.getFilePath());
    }
}
