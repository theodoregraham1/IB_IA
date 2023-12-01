import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamPaper extends PDFInterface {
    public ExamPaper(String fileName, String dirName) {
        super(fileName, dirName);

        try {
            PDDocument document = super.getDocument();

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
