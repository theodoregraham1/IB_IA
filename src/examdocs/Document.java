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
     * Creates new examdocs.Document instance for a specific pdf file and checks that it is valid
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
        PDDocument pdf = Loader.loadPDF(new File(filePath));

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
    public BufferedImage[] getImages(int startPage, int endPage) {
        if (checkImageDir()) {
            return getImagesFromFile(startPage, endPage);
        }

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
    public BufferedImage[] getImages() {
        return getImages(0, -1);
    }

    /**
     * Saves all the images for this pdf to files in the same parent directory as the pdf
     * in a directory called '/images/'
     * @return a boolean for whether the access was successful
     */
    public boolean saveAsImages() {
        String outputPath = dirPath + Constants.PAGES_DIR_NAME;

        // Make the output directory
        File outputDir = new File(outputPath);

        // If the output directory has not been made, make it
        if (!(outputDir.exists())) {
            // Guard clause
            if (!outputDir.mkdirs()) {
                return false;
            }
        }

        try (PDDocument document = getDocument()) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int numberOfPages = document.getNumberOfPages();

            // Save all pages as images
            BufferedImage pageImage;
            for (int i=0; i<numberOfPages; i++) {
                pageImage = pdfRenderer.renderImageWithDPI(i, Constants.DPI, ImageType.RGB);

                ImageIO.write(
                        pageImage,
                        Constants.IMAGE_IO_FORMAT,
                        new File(Constants.PAGE_FILE_FORMAT.formatted(outputPath, i)));
            }

            logger.log(Level.INFO, "%d images from PDF (with file path %s) saved to %s\n".formatted(
                    numberOfPages, dirPath + fileName, outputDir.getPath()));

            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return false;
    }

    /**
     * Checks whether there are already files in this pdf's images directory
     * @return a boolean value for whether the files have been saved
     */
    public boolean checkImageDir() {
        // Check if this has already been split to images

        boolean saved;

        File imagesDir = new File(dirPath + Constants.PAGES_DIR_NAME);

        File[] files = imagesDir.listFiles();
        if (files == null)
            saved = false;
        else
            saved = imagesDir.exists()
                    && files.length > 0;

        return saved;
    }

    /**
     * Gets the images for this PDF, but without rendering it and attempts to find them in its file system
     * @param startPage the index of the first page to convert (inclusive)
     * @param endPage the index of the last page to convert (exclusive), -1 to convert all the pages
     * @return an array of the images found, null if there has been an error
     */
    private BufferedImage[] getImagesFromFile(int startPage, int endPage) {
        // Find where the images are
        String imagesPath = dirPath + Constants.PAGES_DIR_NAME;

        File imagesDir = new File(imagesPath);

        try {
            // Get all the files in the directory
            File[] files = imagesDir.listFiles();
            if (files == null) {
                throw new RuntimeException("No images in image directory of PDF");
            }
            int numFiles = files.length;

            // Make sure page indexes are valid
            if ((endPage != -1)
                    && (endPage < 0
                    || endPage > numFiles
                    || startPage > endPage)) {
                throw new IOException("endPage for image reading must be greater than startPage or -1 for all pages");
            } else if (endPage == -1) {
                endPage = numFiles;
            }

            int difference = endPage - startPage;

            // Read images
            BufferedImage[] images = new BufferedImage[difference];

            // Beware, this relies on images being alphabetical in dictionary
            for (int i = 0; i < difference; i++) {
                images[i] = ImageIO.read(files[startPage + i]);
            }

            return images;
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }

        return null;
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

    /**
     * Returns the path to the directory where this document is stored, from the root directory
     * @return a String of the directory path
     */
    public String getDirPath() {
        return dirPath;
    }

    @Override
    public int compareTo(File file) {
        return file.getPath().compareTo(this.getFilePath());
    }
}
