package GUI;

import examdocs.ExamPaper;
import examdocs.QuestionPaper;
import utils.MultiValueMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

// This was used in one branch to be a superclass to SplitPaperPage and SplitSchemePage,
// however this does not gel with the UI designer
// UI designer source code could be moved but I don't fancy messing with that
public abstract class SplitPDFPage extends JFrame
        implements ChangeListener, ActionListener, WindowListener {
    protected final QuestionPaper document;

    // private final Stack<>
    protected final HashMap<Integer, MultiValueMap<Integer, Color>> allLines;
    protected final ArrayList<int[]> questions;
    protected int currentPage = 0;
    protected int startPage;
    protected int startPercentage;
    protected int questionNumber = 1;
    protected boolean inSplit = false;
    protected int currentLinePercentage = 0;
    protected LinedImageScroller pageComponent;

    public SplitPDFPage(QuestionPaper paper) {
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

    public void saveToPaper(int[] data) {
        document.saveQuestion(
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5]
        );
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

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Save paper?",
                "Window closing",
                JOptionPane.YES_NO_OPTION
        );

        switch (choice) {
            case JOptionPane.YES_OPTION -> saveAllToPaper(questions);
            case JOptionPane.NO_OPTION -> {

            }
        }

        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        this.repaint();
    }

    @Override
    public void windowActivated(WindowEvent e) {
        this.repaint();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        this.repaint();
    }

    protected abstract JButton getNextPageButton();
    protected abstract JButton getPreviousPageButton();
    protected abstract JSlider getPercentageSlider();
    protected abstract JLabel getPercentageDisplay();
    protected abstract JLabel getCurrentPageLabel();
    protected abstract JScrollPane getPaperImagePane();
}
