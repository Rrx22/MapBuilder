package nl.rrx;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private int[][] tileMap;
    private int currentTile;

    SetTileButtonListener setTileButtonListener = new SetTileButtonListener(0);

    private static final List<String> TILE_IMAGE_PATHS = new ArrayList<>();

    private ImageIcon[] tileIcons;

    public MapBuilder() throws HeadlessException {
        super("Map Builder");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tileMap = new int[50][50];
        currentTile = 0;

        getAllImageNames();

        loadTileIcons();
        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout());

        JPanel mapPanel = new JPanel(new GridLayout(50, 50));

        for (int row = 0; row < 50; row++) {
            for (int col = 0; col < 50; col++) {
                JButton mapIcon = new JButton();
                mapIcon.setIcon(tileIcons[tileMap[row][col]]);
                mapIcon.addActionListener(new TileButtonListener(row, col));
                mapPanel.add(mapIcon);
            }
        }

        JPanel imageSelectionPanel = new JPanel(new GridLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        imageSelectionPanel.setMaximumSize(new Dimension(800, 400));
        imageSelectionPanel.setSize(800, 400);
        for (int i = 0; i < TILE_IMAGE_PATHS.size(); i++) {
            JButton tileButton = new JButton(tileIcons[i]);
            tileButton.addActionListener(new SetTileButtonListener(i));
            imageSelectionPanel.add(tileButton, gbc);
            gbc.gridy++;
            if (i > 10) {
                gbc.gridx++;
            }
        }

        JButton saveButton = new JButton("Save map");
        saveButton.addActionListener(new SaveButtonListener());
        imageSelectionPanel.add(saveButton);

        add(mapPanel, BorderLayout.CENTER);
        add(imageSelectionPanel, BorderLayout.SOUTH);
    }

    private void loadTileIcons() {
        tileIcons = new ImageIcon[TILE_IMAGE_PATHS.size()];

        for (int i = 0; i < TILE_IMAGE_PATHS.size(); i++) {
            ImageIcon imageIcon = new ImageIcon(MapBuilder.class.getClassLoader().getResource(TILE_IMAGE_PATHS.get(i)));
            Image scaledImage = imageIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            tileIcons[i] = new ImageIcon(scaledImage);
        }
    }

    private void getAllImageNames() {
        URL imagesDir = MapBuilder.class.getClassLoader().getResource("images");
        try {
            Path imagesPath = Paths.get(imagesDir.toURI());
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(imagesPath)) {
                for (Path file : ds) {
                    String path = "images/" + file.getFileName().toString();
                    TILE_IMAGE_PATHS.add(path);
                    System.out.println(path);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private ImageIcon resizeImage(ImageIcon originalIcon, int width, int height) {
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    // ACTION LISTENERS
    private class SetTileButtonListener implements ActionListener {
        private int tileIdx;

        public SetTileButtonListener(int tileIdx) {
            this.tileIdx = tileIdx;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            currentTile = tileIdx;
        }
    }

    private class TileButtonListener implements ActionListener {
        private int row;
        private int col;

        public TileButtonListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tileMap[row][col] = currentTile;
            ImageIcon resizedIcon = resizeImage(tileIcons[currentTile], 32, 32);
            ((JButton) e.getSource()).setIcon(resizedIcon);
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Open a dialog for the user to input the file name
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
                for (int row = 0; row < 50; row++) {
                    for (int col = 0; col < 50; col++) {
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
