import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PDFInterface {
    protected final String filePath;
    protected final String dirPath;


    public PDFInterface(String fileName, String dirPath) {
        this.dirPath = dirPath;
        this.filePath = fileName;
    }

    protected PDDocument getDocument() throws IOException {
        return PDFInterface.getDocument(this.getFilePath());
    }

    protected static PDDocument getDocument(String filePath) throws IOException {
        // Returns a PDDocument from that file path
        File file = new File(filePath);

        if (!(file.exists())) {
            throw new FileNotFoundException("File at path %s does not exist".formatted(filePath));
        }

        PDDocument pdf = Loader.loadPDF(new File(filePath));
        System.out.printf("PDF loaded with file path: %s", filePath);

        return pdf;
    }

    public String getText() {
        String text;

        try {
            PDDocument document = this.getDocument();
            PDFTextStripper textStripper = new PDFTextStripper();

            text = textStripper.getText(document);

            document.close();

            System.out.printf("Text stripped from PDF with file path: %s", this.getFilePath());
        } catch (IOException e) {
            text = "";
            e.printStackTrace();
        }

        return text;
    }

    /*
    public void saveAsImages() {

    }*/

    public String getFilePath() {
        return dirPath + filePath;
    }
}
