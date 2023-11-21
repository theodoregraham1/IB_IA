import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;

public class SplitPDFHorizontally {

    public static void main(String[] args) {
        try {
            // Specify the path to the existing PDF file
            String inputFilePath = "./papers/GCSE/June-2013/Question-paper/Questionpaper-Paper1-June2017.pdf";

            // Load the existing PDF document
            PDDocument inputDocument = Loader.loadPDF(new File(inputFilePath));

            // Create a new PDF document for the split pages
            PDDocument outputDocument = new PDDocument();

            for (PDPage inputPage : inputDocument.getPages()) {
                // Create two new pages for each existing page
                PDPage topHalfPage = new PDPage(inputPage.getMediaBox());
                PDPage bottomHalfPage = new PDPage(inputPage.getMediaBox());

                // Add the new pages to the output document
                outputDocument.addPage(topHalfPage);
                outputDocument.addPage(bottomHalfPage);

                try (
                        // Create content streams for the new pages
                        PDPageContentStream topContentStream = new PDPageContentStream(outputDocument, topHalfPage);
                        PDPageContentStream bottomContentStream = new PDPageContentStream(outputDocument, bottomHalfPage)
                ) {
                    // Draw the top half of the content on the top page
                    topContentStream.drawForm(new PDFormXObject(inputPage.getMetadata()));

                    // Draw the bottom half of the content on the bottom page
                    bottomContentStream.drawForm(new PDFormXObject(inputPage.getMetadata()));
                    bottomContentStream.transform(Matrix.getTranslateInstance(0, -inputPage.getMediaBox().getHeight() / 2));
                }
            }

            // Save the new PDF document
            String outputFilePath = "path/to/your/output/file.pdf";
            outputDocument.save(outputFilePath);

            // Close both input and output documents
            inputDocument.close();
            outputDocument.close();

            System.out.println("PDF split horizontally successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}