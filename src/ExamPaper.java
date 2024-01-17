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

        this.saveAsImages();

        Scanner scanner = new Scanner(System.in);

        int page_number = 0;
        boolean ended = false, inQuestion = false;

        while (!ended) {
            System.out.printf("Page number %d reached\n", page_number);

            if (inQuestion) {
                System.out.print("Does a question end here? ");
            } else {
                System.out.print("Does a question start here? ");
            }


            // Get the next image

            // Check if a question ends on this image

            // File imagesDir = new File(this.dirPath + IMAGES_DIR_NAME);

            //File[] images = imagesDir.listFiles();
        }
    }
}
