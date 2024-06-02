package nl.rrx.actions;

import nl.rrx.MapBuilder;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveItemListener implements ActionListener {
    private final MapBuilder mapBuilder;

    public SaveItemListener(MapBuilder mapBuilder) {
        this.mapBuilder = mapBuilder;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String fileName = JOptionPane.showInputDialog(mapBuilder, "Enter file name:");
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
            for (int row = 0; row < MapBuilder.MAX_ROWS; row++) {
                for (int col = 0; col < MapBuilder.MAX_COLS; col++) {
                    writer.write(String.format("%02d", mapBuilder.tileMap[row][col]));
                    writer.write(" ");
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(mapBuilder, "Map saved successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mapBuilder, "Error saving map to file!");
        }
    }
}
