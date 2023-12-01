import org.apache.commons.logging.Log;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

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
}
