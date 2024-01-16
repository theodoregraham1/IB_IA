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

    public void saveAsImages() {
        if (!(imagesSaved)) {
            imagesSaved = document.saveAsImages();
            if (imagesSaved) logger.log(Level.INFO, "Saved images from paper to directory");
        }
    }

    public void makeQuestions() {
        // TODO: Split the images into questions

        this.saveAsImages();

        // Get the next image
        // Check if a question ends on this image


        // File imagesDir = new File(this.dirPath + IMAGES_DIR_NAME);

        //File[] images = imagesDir.listFiles();
    }
}
