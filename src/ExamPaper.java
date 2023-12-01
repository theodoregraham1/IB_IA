import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamPaper extends PDFInterface {
    public ExamPaper(String fileName) {
        super(fileName, Logger.getLogger(String.format("ExamPaper-%s", fileName)));

        try {
            PDDocument document = super.getDocument();

            document.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.toString());
        }

    }


}
