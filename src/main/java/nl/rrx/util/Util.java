package nl.rrx.util;

import javax.swing.ImageIcon;
import java.awt.Image;

public class Util {

    private Util() {}

    public static ImageIcon scaledImage(ImageIcon originalIcon, int width, int height) {
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        return new ImageIcon(resizedImage);
    }
}
