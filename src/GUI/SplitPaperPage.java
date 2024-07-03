package GUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import examdocs.ExamPaper;
import utils.InputValidation;
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

// TODO: Throw splits on a stack and have a back button
// TODO: Allow user to cut off footers and headers in multi-page questions (stretch)
// TODO: Allow cutting off the vertical sides (stretch)
// TODO: Allow loading of lines from a current ExamPaper

public class SplitPaperPage extends JFrame
        implements ActionListener, ChangeListener {
    private final ExamPaper paper;
    // private final Stack<>
    private final HashMap<Integer, MultiValueMap<Integer, Color>> allLines;
    private final ArrayList<int[]> questions;
    private int marksSum = 0;

    private int currentPage = 0;
    private int startPage;
    private int startPercentage;

    private int questionNumber = 1;
    private boolean inQuestion = false;

    private int currentLinePercentage = 0;

    private LinedImageScroller pageComponent;

    private JPanel mainPanel;
    private JComboBox<String> anchorSelection;
    private JButton savePaperButton;
    private JScrollPane paperImagePane;
    private JLabel pageLabel;
    private JLabel totalMarks;
    private JSlider percentageSlider;
    private JButton confirmPercentageButton;
    private JLabel percentageSliderLabel;
    private JLabel percentageDisplay;
    private JButton previousPageButton;
    private JButton nextPageButton;
    private JLabel currentPageLabel;
    private JButton undoButton;
    private JButton redoButton;

    public SplitPaperPage(ExamPaper paper) {
        this.paper = paper;
        this.allLines = new HashMap<>(paper.length());
        this.questions = new ArrayList<>();

        // Set JFrame properties
        $$$setupUI$$$();
        setTitle("Exams Manager - Split paper");
        setSize(1200, 600);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        savePaperButton.addActionListener(this);
        confirmPercentageButton.addActionListener(this);
        previousPageButton.addActionListener(this);
        nextPageButton.addActionListener(this);

        percentageSlider.addChangeListener(this);

        paperImagePane.setWheelScrollingEnabled(true);

        setVisible(true);

        // Set the start of all lines
        for (Integer line : allLines.keySet()) {
            allLines.put(line, new MultiValueMap<>());
            allLines.get(line).put(0, Color.RED);
        }

        setPageImage(currentPage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmPercentageButton) {
            if (inQuestion) {
                saveQuestion();
                inQuestion = false;
            } else {
                startPercentage = currentLinePercentage;
                startPage = currentPage;

                percentageSlider.setMinimum(startPercentage + 1);
                addLine(currentPage, startPercentage, Color.GREEN);

                inQuestion = true;
            }
        } else if (e.getSource() == previousPageButton && currentPage > 0) {
            alterPage(-1);
        } else if (e.getSource() == nextPageButton && currentPage < paper.length()) {
            alterPage(1);
        } else if (e.getSource() == savePaperButton) {
            saveAllToPaper(questions);
        } else if (e.getSource() == anchorSelection) {
            Object selectedItem = anchorSelection.getSelectedItem();

        }
    }

    public void saveQuestion() {
        String markString = "a";
        while (!InputValidation.isNumeric(markString)) {
            markString = JOptionPane.showInputDialog(this, "Marks for question:");
        }

        int mark = Integer.parseInt(markString);
        updateMarks(mark);

        questions.add(new int[]{
                questionNumber,
                startPage,
                startPercentage,
                currentPage,
                currentLinePercentage,
                mark
        });

        pageComponent.editHorizontalLine(startPercentage, Color.GREEN, Color.BLACK);
        pageComponent.editHorizontalLine(currentLinePercentage, Color.RED, Color.BLACK);

        percentageSlider.setMinimum(currentLinePercentage);
        pageComponent.addHorizontalLine(currentLinePercentage, Color.RED);

        questionNumber++;
        startPage = currentPage;
        startPercentage = -1;
    }

    public void updateMarks(int mark) {
        marksSum += mark;
        totalMarks.setText("Number of marks: " + mark);
    }

    public void saveAllToPaper(ArrayList<int[]> allData) {
        paper.clearQuestions();
        for (int[] d: allData) {
            saveToPaper(d);
        }
    }

    public void saveToPaper(int[] data) {
        paper.saveQuestion(
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5]
        );
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int newLinePercentage = percentageSlider.getValue();


        if (newLinePercentage != startPercentage) {
            percentageDisplay.setText("Current percentage: " + newLinePercentage);

            pageComponent.editHorizontalLine(currentLinePercentage, newLinePercentage, Color.RED);

            currentLinePercentage = newLinePercentage;
        }
    }

    private void setPageImage(int pageNumber) {
        currentPageLabel.setText("Page: " + pageNumber);

        BufferedImage image = paper.getPage(pageNumber).getImage();

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
        percentageSlider.setMinimum(minimum);

        percentageSlider.setValue(0);

        paperImagePane.setViewportView(pageComponent);
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

    private void alterPage(int movement) {
        allLines.put(currentPage, pageComponent.getLines());
        currentPage += movement;

        setPageImage(currentPage);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
        paperImagePane = new JScrollPane();
        mainPanel.add(paperImagePane, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        anchorSelection = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Questions");
        defaultComboBoxModel1.addElement("Create Paper");
        defaultComboBoxModel1.addElement("Import Paper");
        anchorSelection.setModel(defaultComboBoxModel1);
        mainPanel.add(anchorSelection, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pageLabel = new JLabel();
        pageLabel.setText("Split Paper");
        mainPanel.add(pageLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(151, 16), null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(152, 11), null, 0, false));
        savePaperButton = new JButton();
        savePaperButton.setText("Save Paper");
        mainPanel.add(savePaperButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        totalMarks = new JLabel();
        totalMarks.setText("Number of marks: 0");
        mainPanel.add(totalMarks, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(151, 16), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        percentageDisplay = new JLabel();
        percentageDisplay.setText("Current percentage: 0");
        panel1.add(percentageDisplay, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(95, 16), null, 0, false));
        nextPageButton = new JButton();
        nextPageButton.setText("Next Page");
        panel1.add(nextPageButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        percentageSlider.setInverted(true);
        percentageSlider.setMajorTickSpacing(25);
        percentageSlider.putClientProperty("JSlider.isFilled", Boolean.FALSE);
        percentageSlider.putClientProperty("Slider.paintThumbArrowShape", Boolean.FALSE);
        panel1.add(percentageSlider, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        percentageSliderLabel = new JLabel();
        percentageSliderLabel.setText("Set split percentage");
        panel1.add(percentageSliderLabel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(95, 32), null, 0, false));
        previousPageButton = new JButton();
        previousPageButton.setText("Previous Page");
        panel1.add(previousPageButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        confirmPercentageButton = new JButton();
        confirmPercentageButton.setText("Confirm percentage");
        mainPanel.add(confirmPercentageButton, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentPageLabel = new JLabel();
        currentPageLabel.setText("Current Page: 0");
        mainPanel.add(currentPageLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(81, 30), null, 0, false));
        undoButton = new JButton();
        undoButton.setText("Undo");
        mainPanel.add(undoButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        redoButton = new JButton();
        redoButton.setText("Redo");
        mainPanel.add(redoButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private void createUIComponents() {
        percentageSlider = new JSlider(JSlider.VERTICAL, 0, 100, 0);
        percentageSlider.setLabelTable(percentageSlider.createStandardLabels(25, 0));
        percentageSlider.setPaintLabels(true);
    }
}
