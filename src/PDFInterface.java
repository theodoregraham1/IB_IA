import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDFInterface {
    protected static final String IMAGES_DIR_NAME = "/images";
    protected static final Logger logger = Logger.getLogger(PDFInterface.class.getName());

    protected final String filePath;
    protected final String dirPath;


    public PDFInterface(String fileName, String dirPath) throws IOException {
        logger.setLevel(Level.FINEST);

        this.dirPath = dirPath;
        this.filePath = fileName;

        try {
            this.getDocument().close();
        } catch (IOException e) {
            throw e;
        }
    }

    protected PDDocument getDocument() throws IOException {
        return PDFInterface.getDocument(this.getFilePath());
    }

    // TODO: Restructure everything to save ImagePDFs as such and remove statics
    protected static PDDocument getDocument(String filePath) throws IOException {
        // Returns a PDDocument from that file path
        File file = new File(filePath);

        if (!(file.exists())) {
            throw new FileNotFoundException("File at path %s does not exist".formatted(filePath));
        }
        PDDocument pdf = Loader.loadPDF(new File(filePath));

        logger.log(Level.FINER, "PDF loaded with file path: %s".formatted(filePath));

        return pdf;
    }

    public String getText() {
        String text;

        try {
            PDDocument document = this.getDocument();
            PDFTextStripper textStripper = new PDFTextStripper();

            text = textStripper.getText(document);

            document.close();

            logger.log(Level.FINER, "Text stripped from PDF with file path: %s".formatted(getFilePath()));
        } catch (IOException e) {
            text = "";

            logger.log(Level.SEVERE, e.toString());
        }

        return text;
    }


    public void saveAsImages() {
        PDFToImage.saveAsImages(filePath, dirPath);
    }

    public String getFilePath() {
        return dirPath + filePath;
    }
}
