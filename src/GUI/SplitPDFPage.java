package GUI;

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

public abstract class SplitPDFPage extends JFrame
        implements ActionListener, ChangeListener {
    protected HashMap<Integer, MultiValueMap<Integer, Color>> allLines;
    protected ArrayList<int[]> splits;

    protected LinedImageScroller pageComponent;

    protected int currentPage = 0;
    protected int currentLinePercentage = 0;
    protected int startPercentage;

    protected boolean inSplit = false;

    // Swing components
    protected JSlider percentageSlider;
    protected JScrollPane paperImagePane;
    protected JLabel currentPageLabel;
    protected JButton previousPageButton;
    protected JButton nextPageButton;
    protected JComboBox<String> anchorSelection;
    protected JLabel percentageDisplay;


    protected SplitPDFPage(int length) {
        this.allLines = new HashMap<>(length);
        this.splits = new ArrayList<>();

        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        anchorSelection.setModel(Constants.getAnchorModel());
        anchorSelection.setSelectedIndex(1);

        // Setup lines
        for (Integer line : allLines.keySet()) {
            allLines.put(line, new MultiValueMap<>());
            allLines.get(line).put(0, Color.RED);
        }

        paperImagePane.setWheelScrollingEnabled(true);

        percentageSlider.addChangeListener(this);

        setPageImage(currentPage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == previousPageButton && currentPage > 0) {
            alterPage(-1);
        } else if (e.getSource() == nextPageButton && currentPage < allLines.size()) {
            alterPage(1);
        }
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

    protected void alterPage(int movement) {
        // Hold current lines
        allLines.put(currentPage, pageComponent.getLines());

        // Update to next page
        currentPage += movement;
        setPageImage(currentPage);
    }

    protected abstract void setPageImage(int pageNumber);

    protected void setPageImage(int pageNumber, BufferedImage image) {
        currentPageLabel.setText("Page: " + pageNumber);


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

    protected void addLine(int page, int percentage, Color color) {
        if (page == currentPage) {
            pageComponent.addHorizontalLine(percentage, color);
        }

        if (!allLines.containsKey(page)) {
            allLines.put(page, new MultiValueMap<>());
        }
        allLines.get(page).put(percentage, color);
    }

    protected void createUIComponents() {
        // Percentage slider
        percentageSlider = new JSlider(JSlider.VERTICAL, 0, 100, 0);
        percentageSlider.setLabelTable(percentageSlider.createStandardLabels(25, 0));
        percentageSlider.setPaintLabels(true);
    }


    public abstract void saveSplit();
}
