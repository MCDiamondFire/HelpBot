package com.diamondfire.helpbot.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Creates a coloured star to be used as a role icon.
 */
public class StarUtil {
    
    private static final BufferedImage STAR;
    private static final int SIZE = 64;
    
    static {
        // Load the star from the resources.
        InputStream inputStream = StarUtil.class.getResourceAsStream("/star.png");

        try {
            assert inputStream != null;
            STAR = ImageIO.read(inputStream);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static BufferedImage create(Color color) {
        BufferedImage bufferedImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        
        g2d.drawImage(STAR, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(color);
        g2d.fillRect(0, 0, SIZE, SIZE);
        g2d.dispose();
        return bufferedImage;
    }
    
}
