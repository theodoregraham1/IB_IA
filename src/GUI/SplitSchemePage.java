package GUI;

import javax.swing.*;

public class SplitSchemePage {
    private JPanel mainPanel;
    private JScrollPane paperImagePane;
    private JComboBox anchorSelection;
    private JLabel pageLabel;
    private JButton savePaperButton;
    private JLabel percentageDisplay;
    private JButton nextPageButton;
    private JSlider percentageSlider;
    private JLabel percentageSliderLabel;
    private JButton previousPageButton;
    private JButton confirmPercentageButton;
    private JLabel currentPageLabel;
    private JButton undoButton;
    private JButton redoButton;
    private JButton viewQuestionButton;

    private void createUIComponents() {
        // Percentage slider
        percentageSlider = new JSlider(JSlider.VERTICAL, 0, 100, 0);
        percentageSlider.setLabelTable(percentageSlider.createStandardLabels(25, 0));
        percentageSlider.setPaintLabels(true);
    }
}
