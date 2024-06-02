package nl.rrx.panels;

import nl.rrx.MapBuilder;
import nl.rrx.actions.LoadItemListener;
import nl.rrx.actions.SaveItemListener;
import nl.rrx.actions.TileMouseListener;
import nl.rrx.actions.TileSelectionButtonListener;
import nl.rrx.actions.ToggleBordersListener;
import nl.rrx.actions.ToggleLargePencilListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

import static nl.rrx.MapBuilder.MAP_IMG_SIZE;
import static nl.rrx.MapBuilder.MAX_COLS;
import static nl.rrx.MapBuilder.MAX_ROWS;
import static nl.rrx.util.Util.scaledImage;

public class PanelFactory {

    public static final int SELECT_IMG_SIZE = 48;

    private final MapBuilder mapBuilder;

    public PanelFactory(MapBuilder mapBuilder) {
        this.mapBuilder = mapBuilder;
    }

    public MenuBar constructMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.add(getFileMenu());
        menuBar.add(getToolsMenu());
        return menuBar;
    }

    public JPanel constructMap() {
        JPanel mapPanel = new JPanel(new GridLayout(MAX_ROWS, MAX_COLS));
        mapPanel.setBackground(Color.BLACK);
        mapPanel.setSize(MAX_ROWS * MAP_IMG_SIZE, MAX_COLS * MAP_IMG_SIZE);
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLS; col++) {
                JLabel label = new JLabel(mapBuilder.tileImages.get(mapBuilder.tileMap[row][col]));
                label.setSize(MAP_IMG_SIZE, MAP_IMG_SIZE);
                label.addMouseListener(new TileMouseListener(mapBuilder, row, col));
                mapPanel.add(label);
                mapBuilder.mapPanelRecords.add(new MapPanelRecord(label, row, col));
            }
        }
        return mapPanel;
    }

    public JPanel constructImageSelection() {
        JPanel imageSelectionPanel = new JPanel(new GridLayout(11, 5));
        imageSelectionPanel.setBackground(Color.BLACK);
        for (int i = 0; i < mapBuilder.tileImages.size(); i++) {
            JButton selectionButton = new JButton(scaledImage(mapBuilder.tileImages.get(i), SELECT_IMG_SIZE, SELECT_IMG_SIZE));
            selectionButton.setBackground(Color.BLACK);
            selectionButton.setPreferredSize(new Dimension(SELECT_IMG_SIZE, SELECT_IMG_SIZE));
            selectionButton.setBorder(new LineBorder(Color.BLACK, 1));
            selectionButton.addActionListener(new TileSelectionButtonListener(mapBuilder, i, selectionButton));
            imageSelectionPanel.add(selectionButton);
        }
        return imageSelectionPanel;
    }

    private Menu getFileMenu() {
        Menu fileMenu = new Menu("File");
        MenuItem loadItem = new MenuItem("Load");
        MenuItem saveItem = new MenuItem("Save");
        loadItem.addActionListener(new LoadItemListener(mapBuilder));
        saveItem.addActionListener(new SaveItemListener(mapBuilder));
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        return fileMenu;
    }

    private Menu getToolsMenu() {
        Menu toolsMenu = new Menu("Tools");
        MenuItem toggleBordersItem = new MenuItem("Toggle borders");
        MenuItem toggleLargePencil = new MenuItem("Toggle large pencil");
        toggleBordersItem.addActionListener(new ToggleBordersListener(mapBuilder));
        toggleLargePencil.addActionListener(new ToggleLargePencilListener(mapBuilder));
        toolsMenu.add(toggleBordersItem);
        toolsMenu.add(toggleLargePencil);
        return toolsMenu;
    }
}
