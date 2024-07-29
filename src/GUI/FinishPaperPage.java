package GUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import examdocs.ExamBoard;
import examdocs.Question;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import utils.ImageHandler;
import utils.MultiValueMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class FinishPaperPage extends JFrame
        implements ActionListener, ChangeListener, FocusListener {
    private final ExamBoard board;
    private final AnchorListener finishedListener;
    private final ArrayList<Question> questions;
    private final ArrayList<BufferedImage> pages;

    private final HashMap<Integer, MultiValueMap<Integer, Color>> allLines;
    private LinedImageScroller pageComponent;

    private int currentQuestion = 0;
    private int currentPage = 0;
    private int finalQuestionPage;
    private int currentLinePercentage;

    private JPanel mainPanel;
    private JScrollPane paperViewPane;
    private JButton previousPageButton;
    private JButton nextPageButton;
    private JButton saveQuestionBtn;
    private JLabel titleLabel;
    private JSlider locationSlider;
    private JPanel topLeftPane;
    private JLabel currentPageLabel;

    public FinishPaperPage(ExamBoard board, AnchorListener finishedListener, ArrayList<Question> questions) {
        this.board = board;
        this.finishedListener = finishedListener;
        this.questions = questions;

        this.pages = new ArrayList<>();
        this.allLines = new HashMap<>();

        // Set JFrame properties
        setTitle("Exams Manager - Create paper");
        setSize(1200, 600);
        setContentPane(mainPanel);

        paperViewPane.setWheelScrollingEnabled(true);

        nextPageButton.addActionListener(this);
        previousPageButton.addActionListener(this);
        saveQuestionBtn.addActionListener(this);

        locationSlider.addChangeListener(this);
        locationSlider.addFocusListener(this);
    }

    private void alterPage(int movement) {
        // Hold current lines
        allLines.put(currentPage, pageComponent.getLines());

        // Update to next page
        currentPage += movement;
        drawPageImage();
    }

    private void drawPageImage() {
        currentPageLabel.setText("Page: " + currentPage);

        BufferedImage image = pages.get(currentPage);

        if (image == null) {
            // TODO: Check PDRectangle calls are using the right units
            image = new BufferedImage((int) PDRectangle.A4.getWidth(), (int) PDRectangle.A4.getHeight(), BufferedImage.TYPE_INT_RGB);

            pages.set(currentPage, image);
            addLine(currentPage, 0, Color.RED);
        }

        pageComponent = new LinedImageScroller(image, paperViewPane.getWidth(), allLines.get(currentPage));
        paperViewPane.setViewportView(pageComponent);

        if (finalQuestionPage <= currentPage) {
            // Get minimum line in this page
            int minimum = 0;
            for (Integer i : allLines.keySet()) {
                if (minimum > i) {
                    minimum = i;
                }
            }

            locationSlider.setMinimum(minimum);
            locationSlider.setValue(0);
            locationSlider.setEnabled(true);
        } else {
            locationSlider.setMinimum(0);
            locationSlider.setValue(0);
            locationSlider.setEnabled(false);
        }
    }

    private void addLine(int page, int percentage, Color color) {
        if (page == currentPage) {
            pageComponent.addHorizontalLine(percentage, color);
        }

        if (!allLines.containsKey(page)) {
            allLines.put(page, new MultiValueMap<>());
        }
        allLines.get(page).put(percentage, color);
    }

    public void drawQuestion() {
        // Change current page to have current question image in the right place
        Image currentPageImage = pageComponent.getMasterImage();
        Graphics g = currentPageImage.getGraphics();

        BufferedImage questionToDraw = ImageHandler.copyImage(
                questions.get(currentQuestion).getImage()
                        .getScaledInstance(currentPageImage.getWidth(this), -1, Image.SCALE_DEFAULT)
        );

        int currentPercentage = locationSlider.getValue();
        int drawHeight = (int) (currentPercentage / 100.0 * currentPageImage.getHeight(this));
        int remainingHeight = currentPageImage.getHeight(this) - drawHeight;

        if (remainingHeight < questionToDraw.getHeight(null)) {
            questionToDraw = questionToDraw.getSubimage(0, 0, questionToDraw.getWidth(), remainingHeight);
        }

        g.drawImage(
                questionToDraw,
                0,
                drawHeight,
                this
        );

        pageComponent.setMasterImage(currentPageImage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == previousPageButton) {
            alterPage(-1);
        } else if (e.getSource() == nextPageButton) {
            alterPage(1);
        } else if (e.getSource() == saveQuestionBtn) {
            // Save question image to page
            pages.set(currentPage, ImageHandler.copyImage(pageComponent.getMasterImage()));

            int startPercentage = locationSlider.getValue();
            int endPercentage = startPercentage + questions.get(currentQuestion).getImage().getHeight();

            // Edit slider
            locationSlider.setMinimum(endPercentage);
            locationSlider.setValue(endPercentage);

            // Make lines
            pageComponent.editHorizontalLine(startPercentage, Color.GREEN, Color.BLACK);
            pageComponent.addHorizontalLine(endPercentage, Color.BLACK);

            currentQuestion++;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int newLinePercentage = locationSlider.getValue();

        pageComponent.editHorizontalLine(currentLinePercentage, newLinePercentage, Color.RED);
        currentLinePercentage = newLinePercentage;
    }

    @Override
    public void focusGained(FocusEvent e) {
        drawQuestion();
    }

    @Override
    public void focusLost(FocusEvent e) {
        drawQuestion();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        paperViewPane = new JScrollPane();
        mainPanel.add(paperViewPane, new GridConstraints(1, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(238, 0), null, 0, false));
        topLeftPane = new JPanel();
        topLeftPane.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(topLeftPane, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        previousPageButton = new JButton();
        previousPageButton.setText("Previous Page");
        topLeftPane.add(previousPageButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nextPageButton = new JButton();
        nextPageButton.setText("Next Page");
        topLeftPane.add(nextPageButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentPageLabel = new JLabel();
        currentPageLabel.setText("Current Page: 0");
        topLeftPane.add(currentPageLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        titleLabel = new JLabel();
        titleLabel.setText("Choose locations for questions on pages");
        mainPanel.add(titleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveQuestionBtn = new JButton();
        saveQuestionBtn.setText("Save Question Location");
        mainPanel.add(saveQuestionBtn, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        locationSlider = new JSlider();
        locationSlider.setInverted(true);
        locationSlider.setMajorTickSpacing(25);
        locationSlider.setOrientation(1);
        locationSlider.setValue(0);
        locationSlider.setValueIsAdjusting(false);
        mainPanel.add(locationSlider, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
