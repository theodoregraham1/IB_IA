package examdocs;

import database.ImageFile;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.Constants;
import utils.FileHandler;

public class Document
        implements Comparable<File> {
    private static final Logger logger = Logger.getLogger(Document.class.getName());

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    private final File documentFile;


    /**
     * Creates new Document instance for a specific pdf file and checks that it is valid
     * @param documentFile the file where this document is stored
     */
    public Document(File documentFile) {
        this.documentFile = documentFile;

        if (!(documentFile.exists())) {
            throw new IllegalArgumentException("File at path %s does not exist".formatted(documentFile));
        }

        // Attempt to open and close document to make sure it works
        try (PDDocument ignored = this.getDocument()) {
            // This is just to test opening and closing, ie does it work as a document
        }
        catch (FileNotFoundException e) {
            FileHandler.makeFile(documentFile);

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns the pdfbox document for this document
     * @return Opened PDDocument for this file
     * @throws IOException if file does not exist or can't be opened
     */
    private PDDocument getDocument()
            throws IOException {

        PDDocument pdf = Loader.loadPDF(documentFile);

        logger.log(Level.FINER, "PDF loaded with file path: %s".formatted(documentFile.getPath()));

        return pdf;
    }

    /**
     * Returns the text content for this document
     * @return all the text
     */
    public String getText() {
        String text;

        try (PDDocument document = getDocument()) {

            PDFTextStripper textStripper = new PDFTextStripper();
            text = textStripper.getText(document);

            logger.log(Level.FINER, "Text stripped from PDF with file path: %s".formatted(getFilePath()));

        } catch (IOException e) {
            text = "";

            logger.log(Level.SEVERE, e.toString());
        }

        return text;
    }

    /**
     * Converts the PDF to an array of images for each page
     * @param startPage the index of the first page to convert (inclusive)
     * @param endPage the index of the last page to convert (exclusive), -1 to convert all the pages
     * @return array of the images
     */
    public BufferedImage[] splitToImages(int startPage, int endPage) {

        try (PDDocument document = getDocument()){
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int numberOfPages = document.getNumberOfPages();
            int end = numberOfPages;

            if ((endPage != -1) && (endPage < 0 || endPage > numberOfPages || startPage > endPage)) {
                throw new IOException("endPage for image reading must be greater than startPage or -1 for all pages");
            }
            if (endPage != -1) {
                end = endPage;
            }

            BufferedImage[] images = new BufferedImage[end-startPage];

            for (int i = startPage; i < end; ++i) {
                images[i] = pdfRenderer.renderImageWithDPI(i, Constants.DPI, ImageType.RGB);
            }

            logger.log(Level.FINER,
                    "%d images from PDF (with file path %s) converted".formatted(numberOfPages, getFilePath()));

            return images;

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }

        return null;
    }

    /**
     * Converts all the pages of the pdf to images
     * @return an array of all the images for the pdf
     */
    public BufferedImage[] splitToImages() {
        return splitToImages(0, -1);
    }

    public void addPage(PDPage page) {
        try (PDDocument document = getDocument()) {
            document.addPage(page);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
    }

    public void addPage(ImageFile data) {
        PDPage page = new PDPage(PDRectangle.A4);
        addPage(page);

        try (
                PDDocument document = getDocument();
                PDPageContentStream contentStream = new PDPageContentStream(document, page)
        ) {
            contentStream.drawImage(PDImageXObject.createFromFileByContent(data.getFile(), document), 0, 0);
            document.save(getFilePath());

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
    }

    /**
     * Returns the full filepath to the file from the root directory
     * @return a String of the filepath
     */
    public String getFilePath() {
        return documentFile.getPath();
    }

    public int length() {
        try (PDDocument document = getDocument()) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            return 0;
        }
    }
    @Override
    public int compareTo(File file) {
        return file.compareTo(this.documentFile);
    }
}
