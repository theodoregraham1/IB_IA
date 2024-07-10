package GUI;

import examdocs.ExamPaper;
import utils.MultiValueMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

// This was used in one branch to be a superclass to SplitPaperPage and SplitSchemePage,
// however this does not gel with the UI designer
// UI designer source code could be moved but I don't fancy messing with that
public abstract class SplitPDFPage extends JFrame
        implements ChangeListener, ActionListener {
    protected final ExamPaper document;

    // private final Stack<>
    protected final HashMap<Integer, MultiValueMap<Integer, Color>> allLines;
    protected final ArrayList<int[]> questions;
    protected int marksSum = 0;
    protected int currentPage = 0;
    protected int startPage;
    protected int startPercentage;
    protected int questionNumber = 1;
    protected boolean inSplit = false;
    protected int currentLinePercentage = 0;
    protected LinedImageScroller pageComponent;

    public SplitPDFPage(ExamPaper paper) {
        this.document = paper;
        this.allLines = new HashMap<>(paper.length());
        this.questions = new ArrayList<>();
    }

    protected void saveAllToPaper(ArrayList<int[]> allData) {
        document.clearQuestions();
        for (int[] d : allData) {
            saveToPaper(d);
        }
    }
    protected abstract void saveToPaper(int[] data);

    protected void setPageImage(int pageNumber) {
        getCurrentPageLabel().setText("Page: " + pageNumber);

        BufferedImage image = document.getPage(pageNumber).getImage();
        JScrollPane paperImagePane = getPaperImagePane();

        if (allLines.containsKey(pageNumber)) {
            pageComponent = new LinedImageScroller(image, 10, paperImagePane.getWidth(), allLines.get(pageNumber));
        } else {
            pageComponent = new LinedImageScroller(image, 10, paperImagePane.getWidth());
        }

        // Get minimum line in this page
        int minimum = 0;
        for (Integer i : allLines.keySet()) {
            if (minimum > i) {
                minimum = i;
            }
        }
        JSlider percentageSlider = getPercentageSlider();
        percentageSlider.setMinimum(minimum);
        percentageSlider.setValue(0);

        paperImagePane.setViewportView(pageComponent);
    }

    protected void alterPage(int movement) {
        // Hold current lines
        allLines.put(currentPage, pageComponent.getLines());

        // Update to next page
        currentPage += movement;
        setPageImage(currentPage);
    }

    protected void addLine(int page, int percentage, Color color) {
        if (page == currentPage) {
            pageComponent.addHorizontalLine(percentage, color);
        }

        if (!allLines.containsKey(page)) {
            allLines.put(page, new MultiValueMap<>());
        }
        allLines.get(page).put(percentage, color);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getPreviousPageButton() && currentPage > 0) {
            alterPage(-1);
        } else if (e.getSource() == getNextPageButton() && currentPage < document.length()) {
            alterPage(1);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int newLinePercentage = getPercentageSlider().getValue();

        if (newLinePercentage != startPercentage) {
            getPercentageDisplay().setText("Current percentage: " + newLinePercentage);

            pageComponent.editHorizontalLine(currentLinePercentage, newLinePercentage, Color.RED);

            currentLinePercentage = newLinePercentage;
        }
    }

    protected abstract JButton getNextPageButton();
    protected abstract JButton getPreviousPageButton();
    protected abstract JSlider getPercentageSlider();
    protected abstract JLabel getPercentageDisplay();
    protected abstract JLabel getCurrentPageLabel();
    protected abstract JScrollPane getPaperImagePane();
}