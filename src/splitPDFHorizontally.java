/*
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;

public class SplitPDFHorizontally {

    public static void main(String[] args) {
        try {
            // Specify the path to the existing PDF file
            String inputFilePath = "path/to/your/existing/file.pdf";

            // Load the existing PDF document
            PDDocument inputDocument = Loader.loadPDF(new File(inputFilePath));

            // Create a new PDF document for the split pages
            PDDocument outputDocument = new PDDocument();

            PDPageTree inputPages = inputDocument.getPages();

            for (PDPage inputPage : inputPages) {
                // Create two new pages for each existing page
                PDPage topHalfPage = new PDPage(new PDRectangle(inputPage.getMediaBox().getWidth(), inputPage.getMediaBox().getHeight() / 2));
                PDPage bottomHalfPage = new PDPage(new PDRectangle(inputPage.getMediaBox().getWidth(), inputPage.getMediaBox().getHeight() / 2));

                // Add the new pages to the output document
                outputDocument.addPage(topHalfPage);
                outputDocument.addPage(bottomHalfPage);

                try (
                        // Create content streams for the new pages
                        PDPageContentStream topContentStream = new PDPageContentStream(outputDocument, topHalfPage);
                        PDPageContentStream bottomContentStream = new PDPageContentStream(outputDocument, bottomHalfPage)
                ) {
                    // Copy the content from the existing page to the top half of the new page
                    topContentStream.drawXObject(inputPage);

                    // Copy the content from the existing page to the bottom half of the new page
                    bottomContentStream.drawXObject(inputPage);
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
 */