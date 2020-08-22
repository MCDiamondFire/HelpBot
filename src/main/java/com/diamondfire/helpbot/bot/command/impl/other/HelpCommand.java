package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.DefinedObjectArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.*;
import com.diamondfire.helpbot.bot.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


public class HelpCommand extends Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information for a certain command or provides you with a list of all commands.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("command")
                                .optional()
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet().addArgument("help",
                new DefinedObjectArgument<>(HelpBotInstance.getHandler().getCommands().values().stream()
                        .map(Command::getName)
                        .toArray(String[]::new)).optional(null));
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        String helpInfo = event.getArgument("help");
        if (helpInfo == null) {

            Map<CommandCategory, EmbedBuilder> categories = new LinkedHashMap<>();
            MultiSelectorBuilder selector = new MultiSelectorBuilder();
            selector.setUser(event.getAuthor().getIdLong());
            selector.setChannel(event.getChannel().getIdLong());

            EmbedBuilder homeBuilder = new EmbedBuilder();
            homeBuilder.setDescription("Commands that are available to you are listed in the pages below. To select a page, react to the message. Any additional questions may be forwarded to Owen1212055");
            homeBuilder.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
            homeBuilder.setFooter("Your permissions: " + PermissionHandler.getPermission(event.getMember()));
            selector.addPage("Home", homeBuilder, true);

            // Initialize the pages in advance so we can get a nice order.
            categories.put(CommandCategory.STATS, new EmbedBuilder());
            categories.put(CommandCategory.SUPPORT, new EmbedBuilder());
            categories.put(CommandCategory.CODE_BLOCK, new EmbedBuilder());
            categories.put(CommandCategory.OTHER, new EmbedBuilder());

            List<Command> commandList = new ArrayList<>(HelpBotInstance.getHandler().getCommands().values());
            commandList.sort(Comparator.comparing(Command::getName));
            for (Command command : commandList) {
                HelpContext context = command.getHelpContext();
                CommandCategory category = context.getCommandCategory();
                if (category != null && command.getPermission().hasPermission(event.getMember())) {
                    EmbedBuilder embedBuilder = categories.get(category);
                    embedBuilder.addField(StringUtil.displayCommand(command) + " " + StringUtil.displayArguments(context), context.getDescription(), false);

                }

            }
            // Remove pages that have nothing you have access to.
            categories.entrySet().removeIf((entry) -> entry.getValue().getFields().size() == 0);
            for (Map.Entry<CommandCategory, EmbedBuilder> entries : categories.entrySet()) {
                EmbedBuilder embedBuilder = entries.getValue();
                CommandCategory category = entries.getKey();
                embedBuilder.setDescription(category.getDescription());
                selector.addPage(category.getName(), embedBuilder, category.getEmoji());
            }
            selector.build().send(event.getJDA());
        } else {
            Command command = HelpBotInstance.getHandler().getCommands().get(helpInfo);
            HelpContext context = command.getHelpContext();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Command Information");
            builder.addField("Name", command.getName(), false);
            builder.addField("Description", context.getDescription(), false);
            builder.addField("Aliases", (command.getAliases().length == 0 ? "None" : String.join(", ", command.getAliases())), false);
            builder.addField("Argument", StringUtil.displayArguments(context), true);
            builder.addField("Category", context.getCommandCategory().toString(), true);
            builder.addField("Role Required", String.format("<@&%s>", command.getPermission().getRole()), true);

            event.getChannel().sendMessage(builder.build()).queue();
        }

    }


}
