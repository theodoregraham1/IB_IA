package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

// Code partially sourced from https://docs.oracle.com/javase/tutorial/uiswing/examples/components/ScrollDemoProject/src/components/ScrollablePicture.java

public class ImageScroller extends JLabel
        implements Scrollable {
    private int maxUnitIncrement = 5;
    private boolean missingPicture = false;
    protected Image masterImage;

    public ImageScroller(BufferedImage i, int m, int width) {
        this(i, width);
        maxUnitIncrement = m;
    }

    public ImageScroller(BufferedImage i, int width) {
        super();

        if (i == null) {
            missingPicture = true;
            masterImage = null;

            setText("No picture found.");
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            setBackground(Color.white);
        } else {
            masterImage = i.getScaledInstance(width, -1, Image.SCALE_SMOOTH);

            super.setIcon(new ImageIcon(masterImage));
            super.setSize(new Dimension(width, masterImage.getHeight(null)));
        }
    }

    public Image getMasterImage() {
        return masterImage;
    }

    public void setMasterImage(Image masterImage) {
        this.masterImage = masterImage.getScaledInstance(getWidth(), -1, Image.SCALE_SMOOTH);
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

    @Override
    public Dimension getPreferredSize() {
        if (missingPicture) {
            return new Dimension(320, 480);
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        //Get the current position.
        int currentPosition;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                    (currentPosition / maxUnitIncrement)
                            * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                    * maxUnitIncrement
                    - currentPosition;
        }
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
