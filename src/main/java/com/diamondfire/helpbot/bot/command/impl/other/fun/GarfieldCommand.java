package com.diamondfire.helpbot.bot.command.impl.other.fun;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

public class GarfieldCommand extends Command {
    
    @Override
    public String getName() {
        return "garfield";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a random garfield comic from Ottelino's garfield API.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        final Date d1 = new GregorianCalendar(1978, Calendar.JUNE, 19).getTime();
        final Date d2 = new GregorianCalendar(1999, Calendar.DECEMBER, 31).getTime();
        Date randomDate = new Date(ThreadLocalRandom.current()
            .nextLong(d1.getTime(), d2.getTime()));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/yyyy-MM-dd");

        builder.setTitle("Garfield Comic");
        builder.setImage(String.format("https://derpystuff.gitlab.io/garf/%s.gif", formatter.format(randomDate)));
        builder.setColor(new Color(252, 166, 28));

        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
    
}
