import java.util.logging.Logger;

public class ExamPaper {
    private static final Logger logger = Logger.getLogger(ExamPaper.class.getName());

    private final String dirPath;
    private final String fileName;
    private boolean imagesSaved;
    private final Document document;

    public ExamPaper(String fileName, String dirPath) {
        this.dirPath = dirPath;
        this.fileName = fileName;

        this.document = new Document(fileName, dirPath);

        this.imagesSaved = document.checkImageDir();
    }

    public void saveAsImages() {
        if (!(imagesSaved)) {
            document.sa;
            imagesSaved = true;
        }
    }

    public void makeQuestions() {
        // TODO: Split the images into questions

        // TODO: Throw runtime exception if there are no images instead of saving them
        this.saveAsImages();

        // File imagesDir = new File(this.dirPath + IMAGES_DIR_NAME);

        //File[] images = imagesDir.listFiles();
    }
}
