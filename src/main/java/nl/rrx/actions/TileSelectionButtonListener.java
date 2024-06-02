package nl.rrx.actions;

import nl.rrx.MapBuilder;

import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TileSelectionButtonListener implements ActionListener {
    private final MapBuilder mapBuilder;
    private final int tileIdx;
    private static JButton currentSelectedButton;

    public TileSelectionButtonListener(MapBuilder mapBuilder, int tileIdx, JButton btn) {
        this.mapBuilder = mapBuilder;
        this.tileIdx = tileIdx;
        if (tileIdx == 0) {
            currentSelectedButton = btn;
            currentSelectedButton.setBorder(new LineBorder(Color.RED, 1));
            mapBuilder.selectedTile = tileIdx;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var newlySelectedButton = ((JButton) e.getSource());
        newlySelectedButton.setBorder(new LineBorder(Color.RED, 1));
        currentSelectedButton.setBorder(new LineBorder(Color.BLACK, 1));
        currentSelectedButton = newlySelectedButton;
        mapBuilder.selectedTile = tileIdx;
    }
}
