package nl.rrx.actions;

import nl.rrx.MapBuilder;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static nl.rrx.util.Util.scaledImage;

public class TileMouseListener implements MouseListener {
    private final MapBuilder mapBuilder;
    private final int row;
    private final int col;
    private static boolean clicked;

    public TileMouseListener(MapBuilder mapBuilder, int row, int col) {
        this.mapBuilder = mapBuilder;
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
        mapBuilder.tileMap[row][col] = mapBuilder.selectedTile;
        ImageIcon resizedIcon = scaledImage(mapBuilder.tileImages.get(mapBuilder.selectedTile), MapBuilder.MAP_IMG_SIZE, MapBuilder.MAP_IMG_SIZE);
        if (mapBuilder.largePencilEnabled) {
            mapBuilder.mapPanelRecords.stream()
                    .filter(r -> r.row() >= row - 1 && r.row() <= row + 1 && r.col() >= col - 1 && r.col() <= col + 1)
                    .forEach(r -> r.label().setIcon(resizedIcon));
        } else {
            ((JLabel) e.getSource()).setIcon(resizedIcon);
        }
    }
}
