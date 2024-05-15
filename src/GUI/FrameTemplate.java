package GUI;

import javax.swing.*;

public class FrameTemplate extends JFrame {
    protected JButton questionsAnchor;
    protected JButton createPaperAnchor;
    protected JButton importPaperAnchor;

    protected final int WIDTH = 1200;
    protected final int HEIGHT = 600;

    protected FrameTemplate(String title, int closeOperation) {
        super.setTitle(title);
        super.setDefaultCloseOperation(closeOperation);

        super.setSize(WIDTH, HEIGHT);

        questionsAnchor = new JButton("Questions");
        questionsAnchor.setBounds(Constants.MARGIN, Constants.MARGIN, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        super.add(questionsAnchor);

        createPaperAnchor = new JButton("Create paper");
        createPaperAnchor.setBounds((WIDTH - Constants.BUTTON_WIDTH)/2, Constants.MARGIN, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        super.add(createPaperAnchor);

        importPaperAnchor = new JButton("Import paper");
        importPaperAnchor.setBounds(WIDTH - Constants.MARGIN - Constants.BUTTON_WIDTH, Constants.MARGIN, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        super.add(importPaperAnchor);
    }
}
