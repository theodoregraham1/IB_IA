import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
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

    public String getText() {
        String text;

        try {
            PDDocument document = super.getDocument();
            PDFTextStripper textStripper = new PDFTextStripper();

            text = textStripper.getText(document);

            document.close();
        } catch (IOException e) {
            text = "";
            logger.log(Level.WARNING, e.toString());
        }

        return text;
    }

    public ArrayList<String> splitToQuestions() {
        Integer questionNumber = 1;
        String currentQuestion = "";
        ArrayList<String> questions = new ArrayList<>();

        // Get list of lines of text as all questions start on a new line
        String text = this.getText();
        String[] lines = text.split("\n");

        for (String l: lines) {
            char[] chars = l.toCharArray();

            try {
                // Assume question number max is 99
                if (((questionNumber < 10) && (questionNumber == Integer.parseInt(Character.toString(chars[0]))))
                        || (questionNumber == Integer.parseInt(CharBuffer.wrap(chars), 0, 2, 10))) {

                    // Add current question to the questions and start a new one
                    if (questionNumber != 1) {
                        questions.add(currentQuestion);
                    }
                    questionNumber++;

                    currentQuestion = l;
                } else {
                    currentQuestion += l;
                }
            } catch (NumberFormatException exception) {
                currentQuestion += l;
            }
        }
        questions.add(currentQuestion);

        return questions;
    }
}
