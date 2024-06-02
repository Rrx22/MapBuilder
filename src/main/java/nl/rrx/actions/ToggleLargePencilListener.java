package nl.rrx.actions;

import nl.rrx.MapBuilder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToggleLargePencilListener implements ActionListener {
    private final MapBuilder mapBuilder;

    public ToggleLargePencilListener(MapBuilder mapBuilder) {
        this.mapBuilder = mapBuilder;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mapBuilder.largePencilEnabled = !mapBuilder.largePencilEnabled;
    }

}
