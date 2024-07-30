package GUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import examdocs.ExamBoard;
import examdocs.FullExam;
import examdocs.Question;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import utils.FileHandler;
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

// TODO: Support for multi-page questions

public class FinishPaperPage extends JFrame
        implements ActionListener, ChangeListener, FocusListener {
    private final ExamBoard board;
    private final AnchorListener finishedListener;
    private final ArrayList<Question> questions;
    private final ArrayList<BufferedImage> pages;
    private int[][] questionData;

    private final HashMap<Integer, MultiValueMap<Integer, Color>> allLines;
    private LinedImageScroller pageComponent;

    private int currentQuestion = 0;
    private int currentPage = 0;
    private int finalQuestionPage = 0;      // TODO: Use this
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
    private JButton redrawQuestionBtn;

    public FinishPaperPage(ExamBoard board, AnchorListener finishedListener, ArrayList<Question> questions) {
        this.board = board;
        this.finishedListener = finishedListener;
        this.questions = questions;

        this.pages = new ArrayList<>();
        this.allLines = new HashMap<>();
        this.questionData = new int[questions.size()][4];

        // Set JFrame properties
        setTitle("Exams Manager - Create paper");
        setSize(1200, 600);
        setContentPane(mainPanel);

        paperViewPane.setWheelScrollingEnabled(true);

        nextPageButton.addActionListener(this);
        previousPageButton.addActionListener(this);
        saveQuestionBtn.addActionListener(this);
        redrawQuestionBtn.addActionListener(this);

        locationSlider.addChangeListener(this);
        locationSlider.addFocusListener(this);
        locationSlider.setPaintLabels(true);

        setVisible(true);
        drawPageImage();
        drawQuestion();
    }

    private void alterPage(int movement) {
        // Hold current lines
        allLines.put(currentPage, pageComponent.getLines());

        // Update to next page
        currentPage += movement;

        locationSlider.setEnabled(currentPage >= finalQuestionPage);

        drawPageImage();
    }

    private void drawPageImage() {
        currentPageLabel.setText("Page: " + currentPage);

        BufferedImage image;

        try {
            image = pages.get(currentPage);
        } catch (IndexOutOfBoundsException e) {
            image = null;
        }

        if (image == null) {
            // Make new page image
            image = new BufferedImage((int) PDRectangle.A4.getWidth(), (int) PDRectangle.A4.getHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics g = image.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());

            // Add to pages
            if (pages.size() <= currentPage) {
                pages.add(currentPage, image);
            } else {
                pages.set(currentPage, image);
            }

            // Add selection line
            addLine(currentPage, 0, Color.RED);
        }
        pageComponent = new LinedImageScroller(image, paperViewPane.getWidth(), allLines.get(currentPage));
        paperViewPane.setViewportView(pageComponent);


        if (finalQuestionPage <= currentPage) {
            // Get minimum line in this page
            int minimum = 0;
            for (Integer i : allLines.get(currentPage).keySet()) {
                if (minimum < i) {
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
        if (page == currentPage && pageComponent != null) {
            pageComponent.addHorizontalLine(percentage, color);
        }

        if (!allLines.containsKey(page) || allLines.get(page) == null) {
            allLines.put(page, new MultiValueMap<>());
        }
        allLines.get(page).put(percentage, color);
    }

    private void drawQuestion(BufferedImage questionImage) {
        // Change current page to have current question image in the right place
        BufferedImage currentPageImage = ImageHandler.copyImage(pages.get(currentPage));
        Graphics g = currentPageImage.getGraphics();

        BufferedImage questionToDraw = ImageHandler.copyImage(
                questionImage.getScaledInstance(currentPageImage.getWidth(this), -1, Image.SCALE_SMOOTH)
        );

        int currentPercentage = locationSlider.getValue();

        // Point at which to start drawing the image
        int drawHeight = (int) (currentPercentage / 100.0 * currentPageImage.getHeight(this));

        // Space below the draw height which the image needs to fit into
        int remainingHeight = currentPageImage.getHeight() - drawHeight;

        if (remainingHeight < questionToDraw.getHeight()) {
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

    private void drawQuestion() {
        drawQuestion(questions.get(currentQuestion).getImage());
    }

    private void saveQuestion() {
        // Save question image to page
        drawQuestion();
        pages.set(currentPage, ImageHandler.copyImage(pageComponent.getMasterImage()));
        finalQuestionPage = currentPage;

        BufferedImage questionImage = questions.get(currentQuestion).getImage();
        double scaleFactor = (double) (pageComponent.getWidth()) / questionImage.getWidth(null);

        int startPage = currentPage;
        int currentPercentage = locationSlider.getValue();
        int startPercentage = currentPercentage;

        pageComponent.editHorizontalLine(startPercentage, Color.RED, Color.BLACK);

        int endPercentage = startPercentage
                + (int) Math.ceil(100 * questionImage.getHeight(null) * scaleFactor / (double) pageComponent.getHeight());

        // If it goes over, draw on next page
        while (endPercentage > 100) {
            // Do lines for previous page
            pageComponent.editHorizontalLine(startPercentage, Color.RED, Color.BLACK);
            addLine(currentPage, 100, Color.BLACK);

            // Move to next page
            alterPage(1);
            pageComponent.editHorizontalLine(0, Color.RED, Color.BLACK);

            if (questionImage.getWidth() != pages.get(currentPage).getWidth()) {
                // Get a correctly scaled instance of the question
                questionImage = ImageHandler.copyImage(
                        questionImage.getScaledInstance(
                                pages.get(currentPage).getWidth(this),
                                -1,
                                Image.SCALE_SMOOTH
                        )
                );
            }

            // Find out how much height has been drawn and how much is left to draw
            int percentageDrawn = 100 - currentPercentage;
            int totalHeightInPercent = ImageHandler.heightToPercentage(pages.get(currentPage), questionImage.getHeight());
            int percentageLeftToDraw = totalHeightInPercent - percentageDrawn;

            int heightDrawn = ImageHandler.percentageToHeight(pages.get(currentPage), percentageDrawn);
            int heightLeftToDraw = questionImage.getHeight() - heightDrawn;

            questionImage = questionImage.getSubimage(
                    0,
                    heightDrawn - 1,
                    questionImage.getWidth(),
                    heightLeftToDraw
            );
            drawQuestion(questionImage);
            pages.set(currentPage, ImageHandler.copyImage(pageComponent.getMasterImage()));

            locationSlider.setMinimum(percentageLeftToDraw);
            locationSlider.setValue(percentageLeftToDraw);

            endPercentage = percentageLeftToDraw;
            currentPercentage = 0;
        }

        // Save parameters of question
        questionData[currentQuestion] = new int[]{
                startPage,
                startPercentage,
                currentPage,
                endPercentage,
        };

        // If all questions are placed, export the paper
        if (currentQuestion >= questions.size() - 1) {
            export();
            return;
        }

        // Edit slider
        locationSlider.setMinimum(endPercentage);
        locationSlider.setValue(endPercentage);

        // Make lines
        pageComponent.addHorizontalLine(endPercentage, Color.BLACK);

        currentQuestion++;
        drawQuestion();
    }

    private void export() {
        String name = JOptionPane.showInputDialog("Name of new exam:")
                .strip().replaceAll(" ", "-");

        FullExam result = board.addPaper(name, pages.toArray(new Image[0]), questions.toArray(new Question[0]), questionData);

        FileHandler.openFileOnDesktop(result.getPaper().getDocumentFile());
        FileHandler.openFileOnDesktop(result.getScheme().getDocumentFile());

        finishedListener.getPage(Constants.VIEW_PAPERS);

        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == previousPageButton) {
            alterPage(-1);

        } else if (e.getSource() == nextPageButton) {
            alterPage(1);

        } else if (e.getSource() == saveQuestionBtn) {
            saveQuestion();

        } else if (e.getSource() == redrawQuestionBtn) {
            drawQuestion();
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
        mainPanel.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        paperViewPane = new JScrollPane();
        mainPanel.add(paperViewPane, new GridConstraints(1, 0, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(238, 0), null, 0, false));
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
        mainPanel.add(saveQuestionBtn, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        locationSlider = new JSlider();
        locationSlider.setInverted(true);
        locationSlider.setMajorTickSpacing(25);
        locationSlider.setOrientation(1);
        locationSlider.setValue(0);
        locationSlider.setValueIsAdjusting(false);
        mainPanel.add(locationSlider, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        redrawQuestionBtn = new JButton();
        redrawQuestionBtn.setText("Redraw Question");
        mainPanel.add(redrawQuestionBtn, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}