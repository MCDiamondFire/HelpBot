package com.diamondfire.helpbot.bot.memes;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.File;
import java.util.concurrent.*;

public class ReactionHandler extends ListenerAdapter {
    
    private static final long MEMES_CHANNEL = HelpBotInstance.getConfig().getMemesChannel();
    private static final long BEST_MEMES_CHANNEL = HelpBotInstance.getConfig().getBestMemesChannel();
    private static final int UPVOTE_REQUIREMENT = 10;
    
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        
        Message message = event.retrieveMessage().complete();
        
        if (event.getChannel().getIdLong() == MEMES_CHANNEL) {
            
            if (message.getAuthor().getIdLong() == event.getUser().getIdLong() && event.getReactionEmote().toString().equals("RE:upvote(810917656827002880)")) {
                
                event.getReaction().removeReaction(message.getAuthor()).queue();
                
            } else {
                
                int upvotes = 0;
                boolean accepted = false;
                
                for (int i = 0; i < message.getReactions().size(); i++) {
                
                    if (message.getReactions().get(i).getReactionEmote().getName().equals("upvote")) {
                        
                        upvotes += message.getReactions().get(i).getCount();
                    }
                    if (message.getReactions().get(i).getReactionEmote().getName().equals("downvote")) {
        
                        upvotes -= message.getReactions().get(i).getCount();
                    }
                    if (message.getReactions().get(i).getReactionEmote().getName().equals("accepted")) {
                        
                        accepted = true;
                    }
                }
                
                if (upvotes <= -10) {
                    
                    message.delete().queue();
                }
                if (upvotes >= UPVOTE_REQUIREMENT && !accepted) {
                    
                    message.addReaction(event.getGuild().getEmotesByName("accepted", true).get(0)).queue();
                    File file = new File(ExternalFiles.IMAGES_DIR, "meme.png");
                    try {
                        file = message.getAttachments().get(0).downloadToFile(new File(ExternalFiles.IMAGES_DIR, "meme.png")).get(1L, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
    
                    event.getChannel().sendMessage("<@" + message.getAuthor().getId() + ">'s meme got " + UPVOTE_REQUIREMENT + " upvotes and was added to the <#" + BEST_MEMES_CHANNEL + "> channel.").queue();
    
                    EmbedBuilder builder = new EmbedBuilder();
                    if (message.getContentRaw() != "") { builder.setTitle(message.getContentRaw()); }
                    builder.setDescription("https://discord.com/channels/" + message.getGuild().getId() + "/" + message.getChannel().getId() + "/" + message.getId());
                    builder.setImage("attachment://meme.png");
                    builder.setColor(new Color(0, 217, 255));
                    
                    event.getGuild().getTextChannelById(BEST_MEMES_CHANNEL).sendMessage(builder.build()).addFile(file).queue();
                }
            }
        }
    }

}
