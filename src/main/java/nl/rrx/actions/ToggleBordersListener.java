package nl.rrx.actions;

import nl.rrx.MapBuilder;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToggleBordersListener implements ActionListener {
    private final MapBuilder mapBuilder;
    private boolean hasBorder;

    public ToggleBordersListener(MapBuilder mapBuilder) {
        this.mapBuilder = mapBuilder;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Border border = hasBorder ? null : new LineBorder(Color.BLACK, 1);
        mapBuilder.mapPanelRecords.forEach(mpr -> mpr.label().setBorder(border));
        hasBorder = !hasBorder;
    }

}
