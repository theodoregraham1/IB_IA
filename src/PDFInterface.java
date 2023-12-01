import org.apache.commons.logging.Log;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDFInterface {
    protected Logger logger;
    protected final String filePath;


    public PDFInterface(String fileName, Logger logger) {
        this.logger = logger;
        this.filePath = fileName;
    }

    public PDFInterface(String fileName) {
        this.logger = Logger.getLogger(String.format("PDFInterface-%s", fileName));
        this.filePath = fileName;
    }


    protected PDDocument getDocument() throws IOException {
        PDDocument pdf = Loader.loadPDF(new File(this.filePath));
        logger.log(Level.FINE, "PDF loaded with file path: %s".formatted(filePath));
        return pdf;
    }

    public String getText() {
        String text;

        try {
            PDDocument document = this.getDocument();
            PDFTextStripper textStripper = new PDFTextStripper();

            text = textStripper.getText(document);

            document.close();
        } catch (IOException e) {
            text = "";
            logger.log(Level.WARNING, e.toString());
        }

        return text;
    }
}
