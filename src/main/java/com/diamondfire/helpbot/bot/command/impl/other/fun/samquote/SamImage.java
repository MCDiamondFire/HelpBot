package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SamImage {
    protected static final String SAM_NAME = "           SamMan_";
    
    protected static Color getRandNameColor() {
        double colorChance = Math.random();
        
        if (colorChance < 0.1) {
            return new Color(255, 153, 227);
        } else if (colorChance < 0.3) {
            return new Color(255, 225, 76);
        } else if (colorChance < 0.6) {
            return new Color(35, 255, 38);
        } else {
            return new Color(45, 102, 212);
        }
    }
    
    protected static BufferedImage cropSamPfp() throws IOException {
        BufferedImage samPfp = ImageIO.read(ExternalFiles.SAMMAN.toFile());
        
        int pfpWidth = samPfp.getWidth() * 10;
        
        BufferedImage circleBuffer = new BufferedImage(pfpWidth, pfpWidth, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = circleBuffer.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2.setClip(new Ellipse2D.Float(0, 0, pfpWidth, pfpWidth));
        g2.drawImage(samPfp, 0, 0, pfpWidth, pfpWidth, null);
        
        return circleBuffer;
    }
    
    protected static void setupRenderHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
    
    protected static BufferedImage combine(BufferedImage img, BufferedImage img2, BufferedImage circleBuffer, int height) {
        BufferedImage combined = new BufferedImage(Math.max(img.getWidth(), img2.getWidth()) + 10, img.getHeight() + 10, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = combined.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color bgColor = new Color(54,57,63);
        
        BufferedImage bg = new BufferedImage(Math.max(img.getWidth(), img2.getWidth()) + 30, height + 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bgg = bg.createGraphics();
        bgg.setPaint (bgColor);
        bgg.fillRect (0, 0, bg.getWidth(), bg.getHeight());
        bgg.dispose();
        
        //combine images
        
        g.drawImage(bg, 0, 0, bgColor, null);
        g.drawImage(img, 5, 5, bgColor, null);
        g.drawImage(img2, 5, 5, bgColor, null);
        g.drawImage(circleBuffer, 5, 5, height, height, bgColor, null);
        
        return combined;
    }
    
    protected static BufferedImage samManToImage() {
        Font font = new Font("Whitney", Font.PLAIN, 140);
        BufferedImage img2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d2 = img2.createGraphics();
        
        Font font2 = new Font("Whitney", Font.BOLD, 140);
        g2d2.setFont(font2);
        FontMetrics fm2 = g2d2.getFontMetrics();
        
        int width2 = fm2.stringWidth(SamImage.SAM_NAME);
        int height2 = fm2.getHeight();
        
        g2d2.dispose();
        
        img2 = new BufferedImage(width2, height2, BufferedImage.TYPE_INT_ARGB);
        
        g2d2 = img2.createGraphics();
        
        SamImage.setupRenderHints(g2d2);
        
        g2d2.setFont(font);
        
        g2d2.setColor(SamImage.getRandNameColor());
        g2d2.drawString(SamImage.SAM_NAME, 0, fm2.getAscent());
        
        g2d2.dispose();
        
        return img2;
    }
    
    protected static BufferedImage textToImg(String text, int width, int height, FontMetrics fm) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = img.createGraphics();
        
        SamImage.setupRenderHints(g2d);
        
        g2d.setFont(fm.getFont());
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, 0, fm.getAscent() * 2.2f);
        
        g2d.dispose();
        
        return img;
    }
    
    protected static BufferedImage createFull(String _text) throws IOException {
        String text = "           " + _text;
        
        BufferedImage circleBuffer = SamImage.cropSamPfp();
        
        //convert text into image
        BufferedImage fimg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = fimg.createGraphics();
        
        Font font = new Font("Whitney", Font.PLAIN, 140);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        
        int width = fm.stringWidth(text);
        int height = fm.getHeight() * 2;
        
        g2d.dispose();
        
        BufferedImage img = SamImage.textToImg(text, width, height, fm);
        BufferedImage img2 = SamImage.samManToImage();
        
        return SamImage.combine(img, img2, circleBuffer, height);
    }
}
