import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.util.Objects;

public class ExamPaper extends PDFInterface {
    private boolean imagesSaved;

    public ExamPaper(String fileName, String dirName) {
        super(fileName, dirName);

        try {
            PDDocument document = super.getDocument();

            document.close();

            // Check if it has already been split to images
            File imagesDir = new File(this.dirPath + IMAGES_DIR_NAME);

            if (imagesDir.listFiles() == null)
                imagesSaved = false;
            else
                imagesSaved = imagesDir.exists()
                    && Objects.requireNonNull(imagesDir.listFiles()).length > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveAsImages() {
        if (!(imagesSaved)) {
            super.saveAsImages();
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
