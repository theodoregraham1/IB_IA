import utils.Command;
import utils.Commands;
import utils.Constants;

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

    public ExamPaper(String fileName, String dirPath) {
        this.document = new Document(fileName, dirPath);

        this.imagesSaved = document.checkImageDir();

        makeQuestions();
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

    public void makeQuestions() {
        // TODO: Split the images into questions

        // TODO: Make question directory
        questions = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        this.saveAsImages();

        int BUFFER_SIZE = 5;

        BufferedImage[] imagesBuffer = null;
        ArrayList<BufferedImage> currentImages = null;

        int pageNumber = 0, startHeight = 0, endHeight = 0;
        boolean ended = false, inQuestion = false;
        String questionDirPath = document.getDirPath() + Constants.QUESTIONS_DIR_NAME;

        while (!ended) {
            // Use a buffer of images to reduce time taken
            if (pageNumber % BUFFER_SIZE == 0) {
                 imagesBuffer = document.getImages(pageNumber, pageNumber+BUFFER_SIZE);
            }

            // Command line interfacing
            // TODO: Encapsulate/decompose this better
            System.out.printf("Page number %d reached\n", pageNumber);

            if (inQuestion) {
                // Check if a question ends on this image
                System.out.print("Does a question end here? - ");
            } else {
                System.out.print("Does a question start here? - ");
            }

            Command command = null;
            while (command == null) {
                command = Commands.getCommand(scanner.next());

                // Check command is valid
                if (command != null) {
                    if (!(command.equals("yes") || command.equals("no"))) {
                        command = null;
                    }
                } else {
                    System.out.println("Input yes or no");
                }
            }

            if (inQuestion) {
                currentImages.add(imagesBuffer[pageNumber % BUFFER_SIZE]);
            }

            if (command.equals("yes")) {
                System.out.print("Enter line number: ");
                int lineNum = Integer.parseInt(scanner.next());

                if (!inQuestion) {
                    startHeight = lineNum;
                    currentImages = new ArrayList<>();
                    currentImages.add(imagesBuffer[pageNumber % BUFFER_SIZE]);

                } else {
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
                .getSubimage(0, startHeight, width, startHeight);
        BufferedImage lastImage = inputImages[inputImages.length-1]
                .getSubimage(0, 0, width, endHeight);

        int combinedHeight = height*(inputImages.length-2) + startHeight + endHeight;

        BufferedImage combinedImage = new BufferedImage(width, combinedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = combinedImage.getGraphics();

        // Draw first image
        graphics.drawImage(firstImage, 0, 0, null);

        // Draw middle images
        for (int i = 1; i < inputImages.length-1; i++) {
            graphics.drawImage(inputImages[i], 0, height * (i+1), null);
        }

        // Draw last image
        graphics.drawImage(lastImage, 0,height * (inputImages.length+1), null);

        graphics.dispose();

        return combinedImage;
    }

    public Question saveQuestion(BufferedImage questionImage, int questionNumber, String dirPath) {
        File outputFile = new File(Constants.QUESTION_FILE_FORMAT.formatted(dirPath, questionNumber));

        try {
            ImageIO.write(questionImage,
                    Constants.IMAGE_IO_FORMAT,
                    outputFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
            return null;
        }

        return new Question(outputFile, logger);
    }
}
