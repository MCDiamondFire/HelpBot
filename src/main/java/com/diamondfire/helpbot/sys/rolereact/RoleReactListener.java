package com.diamondfire.helpbot.sys.rolereact;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.*;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RoleReactListener extends ListenerAdapter {
    
    private static final long MESSAGE_ID = 762167137775124491L;
    private static final long CHANNEL_ID = 762158470019022858L;
    
    private static final Map<String, Long> roleMap = new HashMap<>();
    
    public RoleReactListener() {
        if (HelpBotInstance.getConfig().isDevBot()) {
            return;
        }
        
        HelpBotInstance.getJda().getTextChannelById(CHANNEL_ID).retrieveMessageById(MESSAGE_ID).queue((msg) -> {
            
            List<Button> buttons = new ArrayList<>();
            for (ReactRole role : ReactRole.values()) {
                String emoji = role.getEmoji();
                Button button = Button.secondary(role.name(), role.getOverride() == null ? StringUtil.smartCaps(role.name().replace('_', ' ')) : role.getOverride());
                
                buttons.add(button.withEmoji(Emoji.fromUnicode(emoji)));
                roleMap.put(button.getId(), role.getRoleID());
            }
            
            msg.editMessage("__**Reaction Roles**__ \nClick to add/remove roles from yourself").setActionRow(buttons).queue();
        });
    }
    
    
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!roleMap.containsKey(event.getComponentId())) {
            return;
        }
        
        long role = roleMap.get(event.getComponentId());
        Guild guild = event.getGuild();
        Role guildRole = event.getGuild().getRoleById(role);
        Member member = event.getMember();
        
        ReplyCallbackAction action = event.deferReply(true);
        if (member.getRoles().contains(guildRole)) {
            action.setContent("Removed the " + guildRole.getAsMention() + " role from you!");
            guild.removeRoleFromMember(UserSnowflake.fromId(member.getIdLong()), guildRole).reason("User unsubscribed to announcement!").queue();
        } else {
            action.setContent("Added the " + guildRole.getAsMention() + " role to you!");
            guild.addRoleToMember(UserSnowflake.fromId(member.getIdLong()), guildRole).reason("User subscribed to announcement!").queue();
        }
        action.queue();
    }
}
