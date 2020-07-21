package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"que", "q"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current queue.")
                .category(CommandCategory.STATS);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder();
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT player,plot,node, (TIMEDIFF(CURRENT_TIMESTAMP(), enter_time) + 0) AS enter FROM support_queue ORDER BY enter_time LIMIT 25;")
                .onQuery((resultTableQueue) -> {
                    int i = 0;
                    do {
                        embed.addField(resultTableQueue.getString("player"), StringUtil.formatTime(resultTableQueue.getLong("enter"), TimeUnit.SECONDS), false);
                        i++;
                    } while (resultTableQueue.next());
                    embed.setTitle(String.format("Players in Queue (%s)", i));
                    embed.setColor(colorAmt(i));
                })
                .onNotFound(() -> {
                    embed.setTitle("Queue is Empty!");
                    embed.setDescription("Keep up the good work");
                    embed.setColor(new Color(0, 234, 23));
                }).execute();

        event.reply(preset);
    }

    private Color colorAmt(int index) {
        switch (index) {
            case 1:
                return new Color(0, 234, 23);
            case 2:
                return new Color(38, 127, 0);
            case 3:
                return new Color(255, 216, 0);
            case 4:
                return new Color(255, 100, 0);
            case 5:
                return new Color(255, 0, 0);
            default:
                return new Color(127, 0, 0);
        }
    }

}

