package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.CommandCategory;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.util.IOUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RyanStanCommand extends AbstractPlayerUUIDCommand {
    @Override
    public String getName() {
        return "ryanstan";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Adds a cool RyanLand jacket to a skin!")
                .category(CommandCategory.OTHER);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected void execute(CommandEvent event, String player) {
        try {
            BufferedImage skin = ImageIO.read(IOUtil.getFileFromSite("https://mc-heads.net/skin/" + player, "skin.png"));
            BufferedImage jacket = ImageIO.read(IOUtil.getFileFromSite("https://raw.githubusercontent.com/KHVulcano/dfnetherite.github.io/master/RyanLand.png", "ryanstan.png"));

            BufferedImage combined = new BufferedImage(skin.getHeight(), skin.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = combined.getGraphics();
            g.drawImage(skin, 0, 0, null);
            g.drawImage(jacket, 0, 0, null);
            File imageFile = ExternalFileUtil.generateFile("ryanstan.png");
            ImageIO.write(combined, "PNG", imageFile);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setImage("attachment://ryanstan.png")
            .setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
            .setFooter("Command made by Vulcanowo");
            event.getChannel().sendFile(imageFile, "ryanstan.png").embed(embed.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
