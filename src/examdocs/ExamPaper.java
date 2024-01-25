package examdocs;

import commands.Command;
import commands.Commands;
import utils.Constants;
import utils.FileHandler;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamPaper {
    private static final Logger logger = Logger.getLogger(ExamPaper.class.getName());

    private boolean imagesSaved;
    private final Document document;
    private ArrayList<Question> questions;

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

        this.imagesSaved = document.checkImageDir();
    }

    /**
     * Saves images from exam paper's document, updating imagesSaved if it was successful. Only saves
     * images if they are not already saved
     */
    public void saveAsImages() {
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
        // TODO: Write the file information to a random access file and then read it back in if it exists
        int BUFFER_SIZE = 5;
        Commands commands = new Commands(new Command[] {
                new Command("end", new String[]{"end", "e"}),
                new Command("start", new String[]{"start", "s"}),
                new Command("exit", new String[]{"exit"}),
                new Command("pass", new String[]{"pass", "p", " "})
        });

        // Make question directory
        String questionDirPath = document.getDirPath() + Constants.QUESTIONS_DIR_NAME;
        FileHandler.clearDirectory(questionDirPath);

        // Save page images
        this.saveAsImages();

        questions = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        BufferedImage[] imagesBuffer = null;
        ArrayList<BufferedImage> currentImages = null;

        int pageNumber = 0, startHeight = 0, endHeight = 0;
        boolean ended = false, inQuestion = false;

        while (!ended) {
            // Use a buffer of images to reduce time taken
            if ((pageNumber % BUFFER_SIZE) == 0) {
                 imagesBuffer = document.getImages(pageNumber, pageNumber + BUFFER_SIZE);
            }

            // Command line interface
            System.out.printf("Page number %d reached\n", pageNumber);

            // Get command
            Command command = null;
            while (command == null) {
                command = commands.scanCommand(scanner);

                // Check that questions are being started/ended at correct times
                if (command.equals("start") && inQuestion) {
                        System.out.println("examdocs.Question cannot be started until current has been ended");
                        command = null;
                } else if (command.equals("end") && !inQuestion) {
                        System.out.println("examdocs.Question must be started before being ended");
                        command = null;
                }
            }

            // Result of command
            if (inQuestion) {
                currentImages.add(imagesBuffer[pageNumber % BUFFER_SIZE]);
            }

            if (command.equals("end") || command.equals("start")) {
                System.out.print("Line number: ");
                int lineNum = Integer.parseInt(scanner.next());

                if (!inQuestion) {
                    // Start creating new question
                    startHeight = lineNum;

                    currentImages = new ArrayList<>();
                    currentImages.add(imagesBuffer[pageNumber % BUFFER_SIZE]);
                } else {
                    // Save the current question
                    endHeight = lineNum;

                    BufferedImage questionImage = createQuestionImage(
                            currentImages.toArray(new BufferedImage[0]),
                            startHeight,
                            endHeight
                            );
                    questions.add(saveQuestion(
                            questionImage,
                            questions.size(),
                            questionDirPath
                            ));
                }

                inQuestion = !inQuestion;
            } else if (command.equals("exit")) {
                ended = true;
            }

            // Get the next image
            pageNumber ++;
        }
    }

    /**
     * Returns a combined image of all the images in a question put together
     * @param inputImages An array of the images for the question, in order
     * @param startHeight The height where the first image is cut off
     * @param endHeight The height where the last image is cut off
     * @return a single Buffered Image made up of all the inputImages joined vertically
     */
    public BufferedImage createQuestionImage(BufferedImage[] inputImages, int startHeight, int endHeight) {
        int width = inputImages[0].getWidth();
        int height = inputImages[0].getHeight();

        // Get cut off images at start and end
        BufferedImage firstImage = inputImages[0]
                .getSubimage(0, startHeight, width, height-startHeight);
        BufferedImage lastImage = inputImages[inputImages.length-1]
                .getSubimage(0, 0, width, endHeight);

        int combinedHeight = height*(inputImages.length-2) + firstImage.getHeight() + lastImage.getHeight();

        // TODO: Make this work properly
        BufferedImage combinedImage = new BufferedImage(width, combinedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = combinedImage.getGraphics();

        // Draw first image
        graphics.drawImage(firstImage, 0, 0, null);

        // Draw middle images
        for (int i = 1; i < inputImages.length-1; i++) {
            graphics.drawImage(
                    inputImages[i],
                    0,
                    firstImage.getHeight() + (height * (i-1)),
                    null);
        }

        // Draw last image
        graphics.drawImage(lastImage, 0,combinedHeight - lastImage.getHeight(), null);

        graphics.dispose();
        logger.log(Level.INFO, "Made question from %d images".formatted(inputImages.length));
        return combinedImage;
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
     * Returns the pdf file location for this paper
     * @return a File for this paper's file
     */
    public File getFile() {
        return new File(document.getFilePath());
    }
}
