package nl.rrx;

import nl.rrx.panels.MapPanelRecord;
import nl.rrx.panels.PanelFactory;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static nl.rrx.util.Util.scaledImage;

public class MapBuilder extends JFrame {

    public static final String IMAGE_SRC_DIR = "images/";
    public static final String INIT_TILE = "grass00.png";
    public static final int MAP_IMG_SIZE = 18;
    public static final int MAX_ROWS = 50;
    public static final int MAX_COLS = 50;

    public final List<MapPanelRecord> mapPanelRecords;
    public final List<ImageIcon> tileImages;
    public final int[][] tileMap;

    public int selectedTile;
    public boolean largePencilEnabled;

    public MapBuilder() throws HeadlessException {
        super("Map Builder");
        setSize(1148, 967);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mapPanelRecords = new ArrayList<>();
        tileMap = new int[MAX_ROWS][MAX_COLS];
        tileImages = loadTileImages();
    }

    public void createGUI() {
        var factory = new PanelFactory(this);

        setLayout(new BorderLayout());
        setMenuBar(factory.constructMenu());
        add(factory.constructMap(), BorderLayout.CENTER);
        add(factory.constructImageSelection(), BorderLayout.EAST);
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
}
