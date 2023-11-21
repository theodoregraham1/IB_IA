import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        /*
        try {
            // Load an existing PDF document
            PDDocument document = new PDDocument();
            document.addPage(new PDPage());
            PDPage page = document.getPage(0);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.newLineAtOffset(25, 700);
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), 12);

            contentStream.showText("hello world");
            contentStream.endText();
            contentStream.close();

            document.save("output.pdf");

            // Close the document
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
b
         */
        ExamPaper paper = new ExamPaper("./papers/GCSE/June-2013/Question-paper/Questionpaper-Paper1-June2017.pdf");
        System.out.println(paper.getText());

        ArrayList<String> questions = paper.splitToQuestions();

        for (String q: questions) {
            System.out.print(q);
        }
    }
}