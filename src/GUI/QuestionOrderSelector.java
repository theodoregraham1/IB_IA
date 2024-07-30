package GUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import examdocs.ExamBoard;
import examdocs.Question;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class QuestionOrderSelector extends JFrame
        implements ListSelectionListener, ActionListener {
    private final ExamBoard board;
    private final ArrayList<Question> questions;
    private final AnchorListener finishedListener;

    private JList<Question> questionsList;
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JButton finishSelectionButton;
    private JScrollPane questionsView;

    public QuestionOrderSelector(ExamBoard board, ArrayList<Question> questions, AnchorListener finishedListener) {
        this.board = board;
        this.questions = questions;
        this.finishedListener = finishedListener;

        // Set JFrame properties
        setTitle("Exams Manager - Create paper");
        setSize(1200, 600);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultListModel<Question> model = new DefaultListModel<>();
        for (Question q : questions) {
            model.addElement(q);
        }
        questionsList.setModel(model);
        questionsList.setDragEnabled(true);
        questionsList.setDropMode(DropMode.INSERT);
        questionsList.setTransferHandler(new QuestionTransferHandler());
        // questionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        questionsList.addListSelectionListener(this);
        finishSelectionButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new FinishPaperPage(board, finishedListener, questions);
        dispose();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (questionsList.getSelectedValue() == null) return;

        questionsView.setViewportView(
                new ImageScroller(questionsList.getSelectedValue().getImage(), questionsView.getWidth())
        );
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
        mainPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        questionsList = new JList();
        questionsList.setDropMode(DropMode.INSERT);
        questionsList.setLayoutOrientation(0);
        questionsList.setSelectionMode(0);
        mainPanel.add(questionsList, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        titleLabel = new JLabel();
        titleLabel.setText("Drag and drop questions into the order that you want");
        mainPanel.add(titleLabel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        questionsView = new JScrollPane();
        mainPanel.add(questionsView, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        finishSelectionButton = new JButton();
        finishSelectionButton.setText("Finish selection");
        mainPanel.add(finishSelectionButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private class QuestionTransferHandler extends TransferHandler {
        private int fromIndex;

        @Override
        protected Transferable createTransferable(JComponent c) {
            if (!questionsList.equals(c)) return null;

            fromIndex = questionsList.getSelectedIndex();
            return new StringSelection(questionsList.getSelectedValue().toString());
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferSupport info) {
            return info.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport info) {
            if (!canImport(info)) {
                return false;
            } else if (!info.getComponent().equals(questionsList)) {
                return false;
            }

            JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
            int toIndex = dl.getIndex();

            if (fromIndex <= toIndex) {
                toIndex--;
            }

            try {
                DefaultListModel<Question> model = (DefaultListModel<Question>) questionsList.getModel();
                Question value = model.elementAt(fromIndex);

                model.removeElementAt(fromIndex);
                model.add(toIndex, value);

                questions.remove(fromIndex);
                questions.add(toIndex, value);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
    }
}