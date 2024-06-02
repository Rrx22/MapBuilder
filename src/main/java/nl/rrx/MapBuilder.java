package nl.rrx;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public static final String INIT_TILE = "water00.png";
    private static final int MAP_IMG_SIZE = 18;
    private static final int MAX_ROWS = 50;
    private static final int MAX_COLS = 50;

    private final List<JButton> tileSelectionButtons;
    private final List<MapPanelRecord> mapPanelRecords;
    private final List<ImageIcon> tileIcons;
    private final int[][] tileMap;

    private int selectedTile;
    private boolean largePencilEnabled;

    public MapBuilder() throws HeadlessException {
        super("Map Builder");
        setSize(1148, 967);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tileSelectionButtons = new ArrayList<>();
        mapPanelRecords = new ArrayList<>();
        tileMap = new int[MAX_ROWS][MAX_COLS];
        tileIcons = loadTileImages();
    }

    public void createGUI() {
        setLayout(new BorderLayout());

        // MENU
        MenuBar menuBar = new MenuBar();
        menuBar.add(getFileMenu());
        menuBar.add(getToolsMenu());

        // MAP
        JPanel mapPanel = new JPanel(new GridLayout(MAX_ROWS, MAX_COLS));
        mapPanel.setBackground(Color.BLACK);
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLS; col++) {
                JLabel label = new JLabel(tileIcons.get(tileMap[row][col]));
                label.setSize(MAP_IMG_SIZE, MAP_IMG_SIZE);
                label.addMouseListener(new TileMouseListener(row, col));
                mapPanel.add(label);
                mapPanelRecords.add(new MapPanelRecord(label, row, col));
            }
        }

        // SELECTION
        JPanel imageSelectionPanel = new JPanel(new GridLayout(10, 5));
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
        add(imageSelectionPanel, BorderLayout.EAST);
    }

    private List<ImageIcon> loadTileImages() {
        List<ImageIcon> icons = new ArrayList<>();
        URL imagesDir = MapBuilder.class.getClassLoader().getResource(IMAGE_SRC_DIR);
        int initIdx = 0;

        try {
            Path imagesPath = Paths.get(imagesDir.toURI());
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(imagesPath)) {
                int i = 0;
                for (Path file : ds) {
                    String fileName = file.getFileName().toString();
                    if (fileName.equals(INIT_TILE)) initIdx = i;
                    System.out.println(i + ": " + IMAGE_SRC_DIR + fileName);

                    ImageIcon imageIcon = new ImageIcon(file.toUri().toURL());
                    icons.add(scaledImage(imageIcon, MAP_IMG_SIZE, MAP_IMG_SIZE));
                    i++;
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                tileMap[i][j] = initIdx;
            }
        }

        return icons;
    }

    private ImageIcon scaledImage(ImageIcon originalIcon, int width, int height) {
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        return new ImageIcon(resizedImage);
    }

    private record MapPanelRecord(JLabel label, int row, int col) {
        public boolean sameLocation(int row, int col) {
            return this.row == row && this.col == col;
        }
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
        private final int row;
        private final int col;
        private static boolean clicked;

        public TileMouseListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            clicked = true;
//            System.out.println(self.getSize());
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
            // not implemented
        }

        private void updateImg(MouseEvent e) {
            tileMap[row][col] = selectedTile;
            ImageIcon resizedIcon = scaledImage(tileIcons.get(selectedTile), MAP_IMG_SIZE, MAP_IMG_SIZE);
            if (largePencilEnabled) {
                mapPanelRecords.stream()
                        .filter(r -> r.row >= row - 1 && r.row <= row + 1 && r.col >= col - 1 && r.col <= col + 1)
                        .forEach(r -> r.label.setIcon(resizedIcon));
            } else {
                ((JLabel) e.getSource()).setIcon(resizedIcon);
            }
        }
    }

    private class LoadItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(MapBuilder.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(MapBuilder.this, "You selected " + fileChooser.getSelectedFile().getName());
            }

            var file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                for (int row = 0; row < MAX_ROWS; row++) {
                    String[] parsedTileNumbers = reader.readLine().split(" ");
                    for (int col = 0; col < MAX_COLS; col++) {
                        tileMap[row][col] = Integer.parseInt(parsedTileNumbers[col]);
                        for (var x : mapPanelRecords) {
                            if (x.sameLocation(row, col)) {
                                x.label.setIcon(tileIcons.get(tileMap[row][col]));
                                break;
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
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
                        writer.write(String.format("%02d", tileMap[row][col]));
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

    private class ToggleBordersListener implements ActionListener {
        private boolean hasBorder;

        @Override
        public void actionPerformed(ActionEvent e) {
            Border border = hasBorder ? null : new LineBorder(Color.BLACK, 1);
            mapPanelRecords.forEach(mpr -> mpr.label.setBorder(border));
            hasBorder = !hasBorder;
        }

    }

    private class ToggleLargePencilListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            largePencilEnabled = !largePencilEnabled;
        }

    }

    private Menu getFileMenu() {
        Menu fileMenu = new Menu("File");
        MenuItem loadItem = new MenuItem("Load");
        MenuItem saveItem = new MenuItem("Save");
        loadItem.addActionListener(new LoadItemListener());
        saveItem.addActionListener(new SaveItemListener());
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        return fileMenu;
    }

    private Menu getToolsMenu() {
        Menu toolsMenu = new Menu("Tools");
        MenuItem toggleBordersItem = new MenuItem("Toggle borders");
        MenuItem toggleLargePencil = new MenuItem("Toggle large pencil");
        toggleBordersItem.addActionListener(new ToggleBordersListener());
        toggleLargePencil.addActionListener(new ToggleLargePencilListener());
        toolsMenu.add(toggleBordersItem);
        toolsMenu.add(toggleLargePencil);
        return toolsMenu;
    }
}
