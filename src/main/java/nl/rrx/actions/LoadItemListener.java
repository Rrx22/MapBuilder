package nl.rrx.actions;

import nl.rrx.MapBuilder;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoadItemListener implements ActionListener {
    private final MapBuilder mapBuilder;

    public LoadItemListener(MapBuilder mapBuilder) {
        this.mapBuilder = mapBuilder;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(mapBuilder);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(mapBuilder, "You selected " + fileChooser.getSelectedFile().getName());
        }

        var file = fileChooser.getSelectedFile();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            for (int row = 0; row < MapBuilder.MAX_ROWS; row++) {
                String[] parsedTileNumbers = reader.readLine().split(" ");
                for (int col = 0; col < MapBuilder.MAX_COLS; col++) {
                    mapBuilder.tileMap[row][col] = Integer.parseInt(parsedTileNumbers[col]);
                    for (var mapPanelRecord : mapBuilder.mapPanelRecords) {
                        if (mapPanelRecord.sameLocation(row, col)) {
                            mapPanelRecord.label().setIcon(mapBuilder.tileImages.get(mapBuilder.tileMap[row][col]));
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
