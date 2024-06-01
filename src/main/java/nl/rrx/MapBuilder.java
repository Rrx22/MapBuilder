package nl.rrx;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MapBuilder extends JFrame {

    public static final String IMAGE_SRC_DIR = "images/";
    private static final int IMG_SIZE_MAP = 16;
    private static final int MAX_ROWS = 50;
    private static final int MAX_COLS = 50;

    private final MapBuilder self;
    private final List<JButton> tileSelectionButtons;
    private final List<ImageIcon> tileIcons;
    private int[][] tileMap;
    private int selectedTile;

    public MapBuilder() throws HeadlessException {
        super("Map Builder");
        setSize(858, 924);
//        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        self = this;
        tileSelectionButtons = new ArrayList<>();
        tileIcons = loadTileImages();
        tileMap = new int[MAX_ROWS][MAX_COLS];
    }

    public void createGUI() {
        setLayout(new BorderLayout());

        // MENU
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        saveItem.addActionListener(new SaveItemListener());
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);

        // MAP
        JPanel mapPanel = new JPanel(new GridLayout(MAX_ROWS, MAX_COLS));
        mapPanel.setBackground(Color.BLACK);
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLS; col++) {
                JLabel label = new JLabel(tileIcons.get(tileMap[row][col]));
                label.setSize(IMG_SIZE_MAP, IMG_SIZE_MAP);
                label.addMouseListener(new TileMouseListener(row, col));
                mapPanel.add(label);
            }
        }

        // SELECTION
        JPanel imageSelectionPanel = new JPanel(new GridLayout(3, 13));
        imageSelectionPanel.setBackground(Color.BLACK);
        for (int i = 0; i < tileIcons.size(); i++) {
            JButton selectionButton = new JButton(scaledImage(tileIcons.get(i), 48, 48));
            selectionButton.setBackground(Color.BLACK);
            selectionButton.setPreferredSize(new Dimension(48, 48));
            selectionButton.setBorder(new LineBorder(Color.BLACK, 1));
            selectionButton.addActionListener(new TileSelectionButtonListener(i));
            imageSelectionPanel.add(selectionButton);
            tileSelectionButtons.add(selectionButton);
        }

        setMenuBar(menuBar);
        add(mapPanel, BorderLayout.CENTER);
        add(imageSelectionPanel, BorderLayout.SOUTH);
    }

    private List<ImageIcon> loadTileImages() {
        List<ImageIcon> icons = new ArrayList<>();
        URL imagesDir = MapBuilder.class.getClassLoader().getResource(IMAGE_SRC_DIR);

        try {
            Path imagesPath = Paths.get(imagesDir.toURI());
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(imagesPath)) {
                for (Path file : ds) {
                    System.out.println(IMAGE_SRC_DIR + file.getFileName().toString());
                    ImageIcon imageIcon = new ImageIcon(file.toUri().toURL());
                    icons.add(scaledImage(imageIcon, IMG_SIZE_MAP, IMG_SIZE_MAP));
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return icons;
    }

    private ImageIcon scaledImage(ImageIcon originalIcon, int width, int height) {
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        return new ImageIcon(resizedImage);
    }

    // ACTION LISTENERS
    private class TileSelectionButtonListener implements ActionListener {
        private int tileIdx;

        public TileSelectionButtonListener(int tileIdx) {
            this.tileIdx = tileIdx;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tileSelectionButtons.get(selectedTile).setBorder(new LineBorder(Color.BLACK, 1));
            tileSelectionButtons.get(tileIdx).setBorder(new LineBorder(Color.RED, 1));
            selectedTile = tileIdx;
        }
    }

    private class TileMouseListener implements MouseListener {
        private int row;
        private int col;
        private static boolean clicked;

        public TileMouseListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            clicked = true;
            System.out.println(self.getSize());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            updateImg(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (clicked) updateImg(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            clicked = false;
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        private void updateImg(MouseEvent e) {
            tileMap[row][col] = selectedTile;
            ImageIcon resizedIcon = scaledImage(tileIcons.get(selectedTile), IMG_SIZE_MAP, IMG_SIZE_MAP);
            ((JLabel) e.getSource()).setIcon(resizedIcon);
        }
    }

    private class SaveItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = JOptionPane.showInputDialog(MapBuilder.this, "Enter file name:");
            if (fileName != null && !fileName.trim().isEmpty()) {
                saveToFile(fileName);
            }
        }

        private void saveToFile(String fileName) {
            String directoryPath = "res/maps";

            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = directoryPath + File.separator + fileName;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + ".txt"))) {
                for (int row = 0; row < MAX_ROWS; row++) {
                    for (int col = 0; col < MAX_COLS; col++) {
                        writer.write(Integer.toString(tileMap[row][col]));
                        writer.write(" ");
                    }
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(MapBuilder.this, "Map saved successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(MapBuilder.this, "Error saving map to file!");
            }
        }
    }
}
