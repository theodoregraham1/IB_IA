package GUI;

import javax.swing.*;

public class IndexPage extends JFrame {

    public IndexPage(int width, int height) {
        super(Constants.indexTitle);

        super.setSize(width, height);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        super.setVisible(true);

        JPanel panel = new JPanel();
    }

    public static void main(String[] args) {
        IndexPage page = new IndexPage(400, 300);
    }
}
