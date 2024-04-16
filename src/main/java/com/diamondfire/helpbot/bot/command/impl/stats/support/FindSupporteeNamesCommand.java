package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.Player;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import com.google.gson.*;

import java.time.LocalDate;
import java.util.*;

public class FindSupporteeNamesCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "snames";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Retrieves a list of names that this player has been a supportee on.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument().name("player|uuid")
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }
    
    @Override
    protected void execute(CommandEvent event, Player player) {
        JsonObject profile = Util.getPlayerProfile(player.name());
        PresetBuilder builder = new PresetBuilder();
        List<NameDateRange> nameRanges = new ArrayList<>();
        Set<String> names = new HashSet<>();
        Date lastDate = null;
        if (profile == null) {
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Player not found!")
            );
            event.reply(builder);
            return;
        }
        
        String mainName = profile.get("name").getAsString();
        
        JsonArray array = profile.get("name_history").getAsJsonArray();
        
        
        for (int i = array.size() - 1; i >= 0; i--) {
            JsonElement nameElement = array.get(i);
            JsonObject obj = nameElement.getAsJsonObject();
            JsonElement changedAt = obj.get("changedToAt");
            if (changedAt == null) {
                nameRanges.add(new NameDateRange(obj.get("name").getAsString(), null, DateUtil.toDate(LocalDate.now())));
                continue;
            }
            
            Date changeDate = DateUtil.toDate(changedAt.getAsLong());
            
            nameRanges.add(new NameDateRange(obj.get("name").getAsString(), changeDate, lastDate));
            lastDate = changeDate;
            
        }
        
        
        PresetBuilder loadingPreset = new PresetBuilder();
        loadingPreset.withPreset(
                new InformativeReply(InformativeReplyType.INFO, "Please wait while previous names are calculated.")
        );
        
        event.getReplyHandler().replyA(loadingPreset).queue((msg) -> {
            for (NameDateRange range : nameRanges) {
                if (range.getAfter() == null) {
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT * FROM support_sessions WHERE name = ? AND time > ? LIMIT 1", (statement) -> {
                                statement.setString(1, range.getName());
                                statement.setDate(2, DateUtil.toSqlDate(range.getBefore()));
                            }))
                            .compile()
                            .run((result) -> {
                                if (!result.isEmpty()) {
                                    names.add(range.getName());
                                }
                            });
                } else if (range.getBefore() == null) {
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT * FROM support_sessions WHERE name = ? AND time < ? LIMIT 1", (statement) -> {
                                statement.setString(1, range.getName());
                                statement.setDate(2, DateUtil.toSqlDate(range.getAfter()));
                            }))
                            .compile()
                            .run((result) -> {
                                if (!result.isEmpty()) {
                                    names.add(range.getName());
                                }
                            });
                } else {
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT * FROM support_sessions WHERE name = ? AND time BETWEEN ? AND ? LIMIT 1", (statement) -> {
                                statement.setString(1, range.getName());
                                statement.setDate(2, DateUtil.toSqlDate(range.getBefore()));
                                statement.setDate(3, DateUtil.toSqlDate(range.getAfter()));
                            }))
                            .compile()
                            .run((result) -> {
                                if (!result.isEmpty()) {
                                    names.add(range.getName());
                                }
                            });
                }
            }
            
            builder.withPreset(
                    new MinecraftUserPreset(mainName),
                    new InformativeReply(InformativeReplyType.INFO, "Player has been helped on", null)
            );
            EmbedUtil.addFields(builder.getEmbed(), names);
            if (names.isEmpty()) {
                builder.getEmbed().addField("", "Player hasn't been helped by anybody!", false);
            }
            
            msg.editMessageEmbeds(builder.getEmbed().build()).setReplace(true).queue();
        });
    }
    
    private static class NameDateRange {
        
        private final String name;
        private final Date before;
        private final Date after;
        
        public NameDateRange(String name, Date before, Date after) {
            this.name = name;
            this.before = before;
            this.after = after;
        }
        
        public Date getBefore() {
            return before;
        }
        
        public Date getAfter() {
            return after;
        }
        
        public String getName() {
            return name;
        }
    }
}
