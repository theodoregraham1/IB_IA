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

import javax.imageio.ImageIO;

public class Document
        implements Comparable<File> {
    private static final Logger logger = Logger.getLogger(Document.class.getName());

    // Initialise logging level
    static {
        logger.setLevel(Level.FINEST);
    }

    private final String fileName;
    private final String dirPath;



    /**
     * Creates new Document instance for a specific pdf file and checks that it is valid
     * @param fileName name of the file not including parent directories
     * @param dirPath route for parent directories
     */
    public Document(String fileName, String dirPath) {
        this.dirPath = dirPath;
        this.fileName = fileName;

        // Attempt to open and close document to make sure it works
        try (PDDocument ignored = this.getDocument()) {}
        catch (FileNotFoundException e) {
            FileHandler.makeFile(new File(dirPath + File.separatorChar + fileName));
        } catch (IOException e) {
            // TODO: More robust error handling
            logger.log(Level.SEVERE, e.toString());
        }
    }

    /**
     * Returns the pdfbox document for this document
     * @return Opened PDDocument for this file
     * @throws IOException if file does not exist or can't be opened
     */
    private PDDocument getDocument()
            throws IOException {
        String filePath = getFilePath();
        File file = new File(filePath);

        if (!(file.exists())) {
            throw new FileNotFoundException("File at path %s does not exist".formatted(filePath));
        }
        PDDocument pdf = Loader.loadPDF(file);

        logger.log(Level.FINER, "PDF loaded with file path: %s".formatted(filePath));

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
        String filePath = getFilePath();

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
                    "%d images from PDF (with file path %s) converted".formatted(numberOfPages, filePath));

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

    public boolean addPage(PDPage page) {
        try (PDDocument document = getDocument()) {
            document.addPage(page);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return false;
    }

    public boolean addPage(ImageFile data) {
        PDPage page = new PDPage(PDRectangle.A4);
        addPage(page);

        try (
                PDDocument document = getDocument();
                PDPageContentStream contentStream = new PDPageContentStream(document, page)
        ) {
            contentStream.drawImage(PDImageXObject.createFromFileByContent(data.getFile(), document), 0, 0);
            document.save(getFilePath());
            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return false;
    }

    /**
     * Returns the full filepath to the file from the root directory
     * @return a String of the filepath
     */
    public String getFilePath() {
        return dirPath + fileName;
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
        return file.getPath().compareTo(this.getFilePath());
    }
}
