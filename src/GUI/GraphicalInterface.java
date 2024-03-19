package GUI;

import javax.swing.*;

public class GraphicalInterface {
    public static void main(String[] args) {
        // Create a frame (window)
        JFrame frame = new JFrame("Simple Swing Application");
        frame.setSize(300, 200); // Set the size of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation

        // Create a label
        JLabel label = new JLabel("Hello, Swing!");
        // Add the label to the frame
        frame.add(label);

        // Create a button
        JButton button = new JButton("Click Me!");
        // Add an action listener to the button
        button.addActionListener(e -> {
            // Action to perform when the button is clicked
            JOptionPane.showMessageDialog(frame, "Button Clicked!");
        });
        // Add the button to the frame
        frame.add(button);

        // Set layout manager (optional)
        frame.setLayout(new java.awt.FlowLayout());

        // Make the frame visible
        frame.setVisible(true);
    }
}
