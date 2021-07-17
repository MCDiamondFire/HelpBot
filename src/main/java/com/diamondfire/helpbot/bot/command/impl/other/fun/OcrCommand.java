package com.diamondfire.helpbot.bot.command.impl.other.fun;

import com.diamondfire.helpbot.HelpBot;
import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.net.*;
import java.util.List;

public class OcrCommand extends Command {
    
    private static final String ENDPOINT = "https://labscore.vercel.app/v3/google/vision/ocr?url=";
    
    @Override
    public String getName() {
        return "ocr";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Recognizes text based on an image.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                        .name("File Link OR Attachment")
                        .optional()
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("url",
                        new SingleArgumentContainer<>(new StringArgument())
                                .optional(null)
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        String imgUrl = event.getArgument("url");
        if (imgUrl == null) {
           List<Message.Attachment> attachments = event.getMessage().getAttachments();
           if (attachments.isEmpty()) {
               event.reply(
                       new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR, "Please provide an attachment or url!"))
               );
               return;
           } else {
               imgUrl = attachments.get(0).getProxyUrl();
           }
        }
    
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ENDPOINT + imgUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-labscore-auth", HelpBotInstance.getConfig().getlabsCoreToken());
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String text = JsonParser.parseString(in.readLine()).getAsJsonObject().get("text").getAsString();
                if (text.isEmpty()) {
                    text = "No text found";
                }
                
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle("OCR Result")
                        .setThumbnail(imgUrl)
                        .setDescription(StringUtil.trim(text, 2048));
                EmbedUtil.labsCoreBranding(builder);
    
                event.getReplyHandler().reply(builder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection.getResponseCode() == 400) {
                    event.reply(
                            new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR, "The attachment/url you provided could not be read. Make sure it's an image."))
                    );
                    return;
                }
            } catch (Exception ignored) {
            }
    
            event.reply(
                    new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR, "The server errored while processing your request. Please try again later."))
            );
        }
    }
}
