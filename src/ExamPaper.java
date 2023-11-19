import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamPaper {
    Logger logger;
    String filePath;

    public ExamPaper(String fileName) {
        logger = Logger.getLogger("ExamPaper");
        try {
            PDDocument document = Loader.loadPDF(new File(fileName));

            logger.log(Level.FINER, String.format("Exam paper %s loaded correctly", fileName));
            document.close();

        } catch(IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

}
