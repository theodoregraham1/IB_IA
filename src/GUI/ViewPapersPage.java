package GUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import examdocs.ExamBoard;
import examdocs.ExamPaper;
import examdocs.FullExam;
import examdocs.Question;
import utils.FileHandler;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ViewPapersPage extends JFrame
        implements ActionListener, ListSelectionListener {
    private ExamBoard board;

    private JPanel secondaryPanel;
    private JPanel papersTopPanel;
    private JLabel papersLabel;
    private JComboBox<String> anchorSelection;
    private JPanel papersBottomPanel;
    private JList<FullExam> papersList;
    private JScrollPane paperImagePane;
    private JLabel totalMarks;
    private JLabel pageLabel;
    private JList<Question> questionsList;
    private JPanel questionsBottomPanel;
    private JPanel questionsTopPanel;
    private JLabel questionsLabel;
    private JPanel mainPanel;
    private JButton viewPaperButton;
    private JButton paperMarkSchemeBtn;
    private JButton questionMarkSchemeBtn;
    private JButton deleteButton;

    public ViewPapersPage(ExamBoard board, ActionListener anchorListener) {
        this.board = board;

        // Set JFrame properties
        $$$setupUI$$$();
        setTitle("Exams Manager - Split Mark Scheme");
        setSize(1600, 800);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make the anchor selection
        anchorSelection.setModel(Constants.getAnchorModel());
        anchorSelection.setSelectedIndex(Constants.VIEW_PAPERS);
        anchorSelection.addActionListener(anchorListener);

        // Make the papers list
        DefaultListModel<FullExam> papersModel = new DefaultListModel<>();
        for (int i = 0; i < board.size(); i++) {
            papersModel.addElement(board.getExam(i));
        }
        papersList.setModel(papersModel);

        // Listeners
        questionsList.addListSelectionListener(this);
        papersList.addListSelectionListener(this);

        questionMarkSchemeBtn.addActionListener(this);
        viewPaperButton.addActionListener(this);
        paperMarkSchemeBtn.addActionListener(this);
        deleteButton.addActionListener(this);

        paperImagePane.setWheelScrollingEnabled(true);

        questionMarkSchemeBtn.setEnabled(false);
        viewPaperButton.setEnabled(false);
        paperMarkSchemeBtn.setEnabled(false);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == questionMarkSchemeBtn) {
            Question markSchemeQuestion = questionsList.getSelectedValue().getMarkSchemeQuestion();

            Image image = markSchemeQuestion.getImage().getScaledInstance(getWidth(), -1, Image.SCALE_FAST);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (image.getHeight(null) > screenSize.height) {
                image = markSchemeQuestion.getImage().getScaledInstance(-1, screenSize.height, Image.SCALE_FAST);
            }

            JOptionPane.showMessageDialog(
                    this,
                    new ImageIcon(image),
                    "Current question",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else if (e.getSource() == viewPaperButton) {
            FullExam exam = papersList.getSelectedValue();

            FileHandler.openFileOnDesktop(exam.getPaper().getDocumentFile());
        } else if (e.getSource() == paperMarkSchemeBtn) {
            FullExam exam = papersList.getSelectedValue();

            FileHandler.openFileOnDesktop(exam.getScheme().getDocumentFile());
        } else if (e.getSource() == deleteButton) {
            int selection = JOptionPane.showConfirmDialog(
                    this,
                    "Delete paper",
                    "Are you sure you want to delete this paper?",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (selection == JOptionPane.OK_OPTION) {
                board.removeExam(papersList.getSelectedValue().toString());

                DefaultListModel<FullExam> model = (DefaultListModel<FullExam>) papersList.getModel();
                model.removeElement(papersList.getSelectedValue());

                questionsList.setModel(new DefaultListModel<>());
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == papersList) {
            FullExam exam = papersList.getSelectedValue();
            if (exam == null) {
                return;
            }

            ExamPaper paper = exam.getPaper();

            DefaultListModel<Question> questionsModel = new DefaultListModel<>();
            for (int i = 0; i < exam.numQuestions(); i++) {
                questionsModel.addElement(paper.getQuestion(i));
            }

            questionsList.setModel(questionsModel);

            totalMarks.setText("Total marks: " + exam.getPaper().totalMarks());

            viewPaperButton.setEnabled(true);
            paperMarkSchemeBtn.setEnabled(true);

        } else if (e.getSource() == questionsList) {
            BufferedImage image = questionsList.getSelectedValue().getImage();
            paperImagePane.setViewportView(
                    new ImageScroller(image, paperImagePane.getWidth())
            );

            questionMarkSchemeBtn.setEnabled(true);
        }
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
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        secondaryPanel = new JPanel();
        secondaryPanel.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(secondaryPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        papersTopPanel = new JPanel();
        papersTopPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        secondaryPanel.add(papersTopPanel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        papersLabel = new JLabel();
        papersLabel.setText("Papers");
        papersTopPanel.add(papersLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paperMarkSchemeBtn = new JButton();
        paperMarkSchemeBtn.setText("View mark scheme");
        papersTopPanel.add(paperMarkSchemeBtn, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewPaperButton = new JButton();
        viewPaperButton.setText("View paper");
        papersTopPanel.add(viewPaperButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        anchorSelection = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        anchorSelection.setModel(defaultComboBoxModel1);
        secondaryPanel.add(anchorSelection, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        papersBottomPanel = new JPanel();
        papersBottomPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        secondaryPanel.add(papersBottomPanel, new GridConstraints(1, 4, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        papersList = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        papersList.setModel(defaultListModel1);
        papersBottomPanel.add(papersList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        paperImagePane = new JScrollPane();
        paperImagePane.setHorizontalScrollBarPolicy(31);
        paperImagePane.setVerticalScrollBarPolicy(22);
        secondaryPanel.add(paperImagePane, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        totalMarks = new JLabel();
        totalMarks.setText("Total marks: N/A");
        secondaryPanel.add(totalMarks, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pageLabel = new JLabel();
        pageLabel.setText("View Papers");
        secondaryPanel.add(pageLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        questionsTopPanel = new JPanel();
        questionsTopPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        secondaryPanel.add(questionsTopPanel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        questionsLabel = new JLabel();
        questionsLabel.setText("Questions: ");
        questionsTopPanel.add(questionsLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        questionMarkSchemeBtn = new JButton();
        questionMarkSchemeBtn.setText("View mark scheme");
        questionsTopPanel.add(questionMarkSchemeBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        questionsBottomPanel = new JPanel();
        questionsBottomPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        secondaryPanel.add(questionsBottomPanel, new GridConstraints(1, 5, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        questionsList = new JList();
        final DefaultListModel defaultListModel2 = new DefaultListModel();
        questionsList.setModel(defaultListModel2);
        questionsBottomPanel.add(questionsList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("Delete Paper");
        secondaryPanel.add(deleteButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
