package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.arguments.value.DefinedStringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.command.permissions.PermissionHandler;
import com.diamondfire.helpbot.components.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.instance.BotInstance;
import com.diamondfire.helpbot.util.BotConstants;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


public class HelpCommand extends Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Gets information for a certain command or provides you with a list of all commands.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OTHER;
    }

    @Override
    public ValueArgument<String> getArgument() {
        return new DefinedStringArg(BotInstance.getHandler().getCommands().values().stream()
                .map(Command::getName)
                .toArray(String[]::new), false, "Command Name");
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        if (event.getParsedArgs().isEmpty()) {
            Map<CommandCategory, EmbedBuilder> categories = new LinkedHashMap<>();
            MultiSelectorBuilder selector = new MultiSelectorBuilder();
            selector.setUser(event.getAuthor().getIdLong());
            selector.setChannel(event.getChannel().getIdLong());

            EmbedBuilder homeBuilder = new EmbedBuilder();
            homeBuilder.setDescription("Commands that are available to you are listed in the pages below. To select a page, react to the message. Any additional questions may be forwarded to Owen1212055");
            homeBuilder.setThumbnail(BotInstance.getJda().getSelfUser().getAvatarUrl());
            homeBuilder.setFooter("Your permissions: " + PermissionHandler.getPermission(event.getMember()));
            selector.addPage("Home", homeBuilder, true);

            // Initialize the pages in advance so we can get a nice order.
            categories.put(CommandCategory.STATS, new EmbedBuilder());
            categories.put(CommandCategory.CODE_BLOCK, new EmbedBuilder());
            categories.put(CommandCategory.OTHER, new EmbedBuilder());

            List<Command> commandList = new ArrayList<>(BotInstance.getHandler().getCommands().values());
            commandList.sort(Comparator.comparing(Command::getName));
            for (Command command : commandList) {
                CommandCategory category = command.getCategory();
                if (category != CommandCategory.HIDDEN && command.getPermission().hasPermission(event.getMember())) {
                    EmbedBuilder embedBuilder = categories.get(category);
                    Argument argument = command.getArgument();
                    String arg = argument instanceof NoArg ? "" : String.format(" <%s>", argument);
                    embedBuilder.addField(BotConstants.PREFIX + command.getName() + arg, command.getDescription(), false);

                }

            }
            for (Map.Entry<CommandCategory, EmbedBuilder> entries : categories.entrySet()) {
                // Remove pages that have nothing you have access to.
                if (entries.getValue().getFields().size() == 0) {
                    categories.remove(entries.getKey());
                    continue;
                }
                EmbedBuilder embedBuilder = entries.getValue();
                CommandCategory category = entries.getKey();
                embedBuilder.setDescription(category.getDescription());
                selector.addPage(category.getName(), embedBuilder, category.getEmoji());
            }
            selector.build().send();
        } else {
            Command command = BotInstance.getHandler().getCommands().get(getArgument().getArg(event.getParsedArgs()));
            Argument argument = command.getArgument();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Command Information");
            builder.addField("Name", command.getName(), false);
            builder.addField("Description", command.getDescription(), false);
            builder.addField("Aliases", (command.getAliases().length == 0 ? "None" : String.join(", ", command.getAliases())), false);
            builder.addField("Argument", String.format("<%s>", argument), true);
            builder.addField("Category", command.getCategory().toString(), true);
            builder.addField("Role Required", String.format("<@&%s>", command.getPermission().getRole()), true);
            event.getChannel().sendMessage(builder.build()).queue();
        }

    }

}
