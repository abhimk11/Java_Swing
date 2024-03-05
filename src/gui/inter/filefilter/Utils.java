package gui.inter.filefilter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Utils {
    public static String getFileExtension(String name) {
        int pointIndex = name.lastIndexOf(".");

        if (pointIndex == -1) {
            return null;
        }

        if (pointIndex == name.length() - 1) {
            return null;
        }
        return name.substring(pointIndex + 1, name.length());
    }

    public static ImageIcon createIcon(String path) {
        URL url = Utils.class.getResource(path);

        if (url == null) {
            System.err.println("Unable to load Image: " + path);
        }
        ImageIcon icon = new ImageIcon(url);

        return icon;
    }

    public static Font createFont(String path) {
        URL url = Utils.class.getResource(path);

        if (url == null) {
            System.err.println("Unable to load font: " + path);
        }
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT,url.openStream());
        } catch (FontFormatException e) {
            System.out.println("Bad format in font file: " + path);
        } catch (IOException e) {
            System.out.println("Unable to read font file");
        }

        return font;
    }
}
