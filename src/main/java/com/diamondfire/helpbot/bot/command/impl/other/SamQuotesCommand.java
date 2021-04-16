package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.DefinedObjectArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.util.IOUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;

import static com.diamondfire.helpbot.util.textgen.CacheData.CacheData; //use this if you want to add more data to ai sam
import static com.diamondfire.helpbot.util.textgen.MarkovManipulation.getNextWord;

public class SamQuotesCommand extends Command {
    
    private static final Random random = new Random();
    
    @Override
    public String getName() {
        return "samquote";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a quote from Sam the Man.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("get"),
                        new HelpContextArgument()
                                .name("submit"),
                        new HelpContextArgument()
                                .name("generate"));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        
        if (event.getArgument("action") == null) {
    
            String[] strings = ExternalFiles.SAM_DIR.list();
            File file = new File(ExternalFiles.SAM_DIR, strings[random.nextInt(strings.length)]);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Sam Quote");
            builder.setImage("attachment://quote.png");
            builder.setColor(new Color(87, 177, 71));
    
            event.getChannel().sendMessage(builder.build()).addFile(file, "quote.png").queue();
    
        } else if (event.getArgument("action").equals("submit")) {
            
            String[] message = event.getMessage().getContentRaw().split("/");
            
            if(message.length != 7 || message[5] == null || message[6] == null || message[5].length() != 18 || message[6].length() != 18) {
    
                PresetBuilder error = new PresetBuilder();
                
                error.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "This is not a samquote!")
                );
                
                event.reply(error);
                
                return;
            }
            
            long channelID = Long.parseLong(message[5]);
            long messageID = Long.parseLong(message[6]);
            
            event.getGuild().getTextChannelById(channelID).retrieveMessageById(messageID).queue((messageText) -> {
                
                if(messageText.getAuthor().getIdLong() == 132092551782989824L) {
                    
                    try {
                        
                        BufferedImage samPfp = ImageIO.read(ExternalFiles.SAMMAN);
                        
                        String text = "           " + messageText.getContentRaw().replaceAll("[^a-zA-Z0-9 ]", "");
    
                        int pfpWidth = samPfp.getWidth() * 10;
    
                        //crop pfp into a circle
    
                        BufferedImage circleBuffer = new BufferedImage(pfpWidth, pfpWidth, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = circleBuffer.createGraphics();
    
                        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g2.setClip(new Ellipse2D.Float(0, 0, pfpWidth, pfpWidth));
                        g2.drawImage(samPfp, 0, 0, pfpWidth, pfpWidth, null);
    
                        //convert text into image
    
                        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    
                        Graphics2D g2d = img.createGraphics();
    
                        Font font = new Font("Whitney", Font.PLAIN, 140);
                        g2d.setFont(font);
                        FontMetrics fm = g2d.getFontMetrics();
    
                        int width = fm.stringWidth(text);
                        int height = fm.getHeight() * 2;
    
                        g2d.dispose();
    
                        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    
                        g2d = img.createGraphics();
    
                        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    
                        g2d.setFont(font);
                        g2d.setColor(Color.WHITE);
                        g2d.drawString(text, 0, fm.getAscent() * 2.2f);
    
                        g2d.dispose();
    
                        //convert SamMan_ into image
    
                        BufferedImage img2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    
                        Graphics2D g2d2 = img2.createGraphics();
    
                        Font font2 = new Font("Whitney", Font.BOLD, 140);
                        g2d2.setFont(font2);
                        FontMetrics fm2 = g2d2.getFontMetrics();
    
                        int width2 = fm2.stringWidth("           SamMan_");
                        int height2 = fm2.getHeight();
    
                        g2d2.dispose();
    
                        img2 = new BufferedImage(width2, height2, BufferedImage.TYPE_INT_ARGB);
    
                        g2d2 = img2.createGraphics();
    
                        g2d2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                        g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                        g2d2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                        g2d2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                        g2d2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2d2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    
                        g2d2.setFont(font);
                        Color nameColor = new Color(45, 102, 212);
                        g2d2.setColor(nameColor);
                        g2d2.drawString("           SamMan_", 0, fm2.getAscent());
    
                        g2d2.dispose();
    
                        //create background image
    
                        BufferedImage combined = new BufferedImage(Math.max(img.getWidth(), img2.getWidth()) + 10, img.getHeight() + 10, BufferedImage.TYPE_INT_ARGB);
    
                        Graphics g = combined.getGraphics();
    
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
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
                        
                        //save image
                        
                        File imageFile = new File(ExternalFiles.SAM_DIR, messageText.getContentRaw().replaceAll("[^a-zA-Z0-9]", "") + ".png");
                        ImageIO.write(combined, "PNG", imageFile);
                        
                        PresetBuilder success = new PresetBuilder();
                        
                        success.withPreset(
                                new InformativeReply(InformativeReplyType.SUCCESS, "Your SamQuote has been added!")
                        );
                        
                        event.reply(success);
                        
                    } catch (IOException e) {
                        
                        e.printStackTrace();
                        
                    }
                    
                } else {
    
                    PresetBuilder error = new PresetBuilder();
    
                    error.withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "This is not a samquote!")
                    );
                    
                    event.reply(error);
                    
                }
                
            });
        } else if (event.getArgument("action").equals("generate")) {
    
            File file = new File("samquotes.txt");
            
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
            ArrayList<String> startingTexts = new ArrayList<>();
    
            String line = null;
            while (true) {
                try {
                    if ((line = br.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                startingTexts.add(line.split(" ")[0]);
            }
            
            String word = startingTexts.get((int) Math.rint(Math.random() * startingTexts.size()));
            
            String string = "";
            for (int i = 0; i < 50; i++) {
                
                if (word == null || word.equals(".")) break;
                string += word + " ";
                try {
                    word = getNextWord(word);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    
            BufferedImage samPfp = null;
            try {
                samPfp = ImageIO.read(ExternalFiles.SAMMAN);
            } catch (IOException e) {
                e.printStackTrace();
            }
    
            String text = "           " + string;
    
            int pfpWidth = samPfp.getWidth() * 10;
    
            //crop pfp into a circle
    
            BufferedImage circleBuffer = new BufferedImage(pfpWidth, pfpWidth, BufferedImage.TYPE_INT_ARGB);
    
            Graphics2D g2 = circleBuffer.createGraphics();
    
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
            g2.setClip(new Ellipse2D.Float(0, 0, pfpWidth, pfpWidth));
            g2.drawImage(samPfp, 0, 0, pfpWidth, pfpWidth, null);
    
            //convert text into image
    
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    
            Graphics2D g2d = img.createGraphics();
    
            Font font = new Font("Whitney", Font.PLAIN, 140);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
    
            int width = fm.stringWidth(text);
            int height = fm.getHeight() * 2;
    
            g2d.dispose();
    
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    
            g2d = img.createGraphics();
    
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    
            g2d.setFont(font);
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, 0, fm.getAscent() * 2.2f);
    
            g2d.dispose();
    
            //convert SamMan_ into image
    
            BufferedImage img2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    
            Graphics2D g2d2 = img2.createGraphics();
    
            Font font2 = new Font("Whitney", Font.BOLD, 140);
            g2d2.setFont(font2);
            FontMetrics fm2 = g2d2.getFontMetrics();
    
            int width2 = fm2.stringWidth("           SamMan_");
            int height2 = fm2.getHeight();
    
            g2d2.dispose();
    
            img2 = new BufferedImage(width2, height2, BufferedImage.TYPE_INT_ARGB);
    
            g2d2 = img2.createGraphics();
    
            g2d2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    
            g2d2.setFont(font);
            
            double colorChance = Math.random();
    
    
            Color nameColor;
            
            if (colorChance < 0.1) {
                
                nameColor = new Color(255, 153, 227);
                
            } else if (colorChance < 0.3) {
                
                nameColor = new Color(255, 225, 76);
                
            } else if (colorChance < 0.6) {
                
                nameColor = new Color(35, 255, 38);
                
            } else {
                
                nameColor = new Color(45, 102, 212);
            }
            
            g2d2.setColor(nameColor);
            g2d2.drawString("           SamMan_", 0, fm2.getAscent());
    
            g2d2.dispose();
    
            //create background image
    
            BufferedImage combined = new BufferedImage(Math.max(img.getWidth(), img2.getWidth()) + 10, img.getHeight() + 10, BufferedImage.TYPE_INT_ARGB);
    
            Graphics g = combined.getGraphics();
    
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
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
    
            File samQuote = new File(ExternalFiles.IMAGES_DIR, "quote.png");
            try {
                ImageIO.write(combined, "PNG", samQuote);
            } catch (IOException e) {
                e.printStackTrace();
            }
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Sam Quote");
            builder.setImage("attachment://quote.png");
            builder.setColor(new Color(87, 177, 71));
    
            event.getChannel().sendMessage(builder.build()).addFile(samQuote, "quote.png").queue();
        
        } else {
            
            String[] strings = ExternalFiles.SAM_DIR.list();
            File file = new File(ExternalFiles.SAM_DIR, strings[random.nextInt(strings.length)]);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Sam Quote");
            builder.setImage("attachment://quote.png");
            builder.setColor(new Color(87, 177, 71));
            
            event.getChannel().sendMessage(builder.build()).addFile(file, "quote.png").queue();
            
        }
        
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("action",
                        new SingleArgumentContainer<>(new DefinedObjectArgument<>("submit", "generate", "get")).optional(null));
    }
    
}
