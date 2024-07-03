package com.diamondfire.helpbot.bot.command.impl.other.fun;

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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static com.diamondfire.helpbot.util.textgen.CacheData.cacheData;
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
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        String[] strings = ExternalFiles.SAM_DIR.list();
        
        File file = new File(ExternalFiles.SAM_DIR, strings[random.nextInt(strings.length)]);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Sam Quote");
        builder.setImage("attachment://quote.png");
        builder.setColor(new Color(87, 177, 71));
    
        event.getChannel().sendMessageEmbeds(builder.build()).addFiles(FileUpload.fromData(file).setName("quote.png")).queue();
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
}
