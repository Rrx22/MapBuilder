package nl.rrx;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MapBuilder mapBuilder = new MapBuilder();
            mapBuilder.createGUI();
            mapBuilder.setVisible(true);
        });

    }
}