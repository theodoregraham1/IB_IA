import utils.Command;
import utils.Commands;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamPaper {
    private static final Logger logger = Logger.getLogger(ExamPaper.class.getName());

    private boolean imagesSaved;
    private final Document document;

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

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

    public void makeQuestions() {
        // TODO: Split the images into questions
        ArrayList<Question> questions = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        this.saveAsImages();

        int BUFFER_SIZE = 5;

        BufferedImage[] imagesBuffer;
        BufferedImage currentImage;

        int page_number = 0, startHeight = 0, endHeight = 0;
        boolean ended = false, inQuestion = false;

        while (!ended) {
            // Use a buffer of images to reduce time taken
            if (page_number % BUFFER_SIZE == 0) {
                 imagesBuffer = document.getImages(page_number, page_number+);
            }
            currentImage = imagesBuffer[page_number % BUFFER_SIZE];

            // Command line interfacing
            // TODO: Encapsulate/decompose this better
            System.out.printf("Page number %d reached\n", page_number);

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

            if (command.equals("yes")) {
                System.out.println("Enter line number:");
                int lineNum = Integer.parseInt(scanner.nextLine());

                if (!inQuestion) {
                    startHeight = lineNum;
                } else {
                    endHeight = lineNum;


                }
                inQuestion = !inQuestion;
            }


            // Get the next image
            page_number ++;
        }
    }

    public Question saveImage(BufferedImage[] images, int startHeight, int endHeight) {
        int width = images[0].getWidth();
        int height = images[0].getHeight();

        BufferedImage firstImage = images[0].getSubimage(0, startHeight, width, startHeight);

        M
    }
}
