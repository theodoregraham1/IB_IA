import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.util.Objects;

public class ExamPaper {
    private final String dirPath;
    private final String fileName;
    private boolean imagesSaved;
    private PDFInterface documentInterface;

    public ExamPaper(String fileName, String dirPath) {
        this.dirPath = dirPath;
        this.fileName = fileName;

        try {
            documentInterface = new PDFInterface(fileName, dirPath);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveAsImages() {
        if (!(imagesSaved)) {
            PDFToImage.saveAsImages(fileName, dirPath);
            imagesSaved = true;
        }
    }

    public void makeQuestions() {
        // TODO: Split the images into questions

        // TODO: Throw runtime exception if there are no images instead of saving them
        this.saveAsImages();

        File imagesDir = new File(this.dirPath + IMAGES_DIR_NAME);

        File[] images = imagesDir.listFiles();
    }
}
