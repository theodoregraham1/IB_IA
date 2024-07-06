package GUI;

import utils.MultiValueMap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class SplitPDFPage extends JFrame {
    protected HashMap<Integer, MultiValueMap<Integer, Color>> allLines;
    protected ArrayList<int[]> questions;

    protected int currentPage = 0;
}
