package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.CommandCategory;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.InformativeReply;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.InformativeReplyType;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.nbs.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class NbsCommand extends Command {

    @Override
    public String getName() {
        return "nbs";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a Codeutils song function.")
                .category(CommandCategory.OTHER);
    }

    @Override
    protected ArgumentSet compileArguments() { return new ArgumentSet(); }

    @Override
    public Permission getPermission() { return Permission.USER; }

    @Override
    public void run(CommandEvent event) {
        TextChannel channel = event.getChannel();
        PresetBuilder preset = new PresetBuilder();
        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR,"You need to attach an nbs file!"));
        EmbedBuilder warning = new EmbedBuilder()
                .addField("Warning!","You need to attach an nbs file!",false)
                .setColor(Color.RED);
        if(event.getMessage().getAttachments().isEmpty()) {
            event.reply(preset);
            return;
        }
        Message.Attachment attachment = event.getMessage().getAttachments().get(0);
        if(!attachment.getFileExtension().equals("nbs")) {
            event.reply(preset);
            return;
        }
        EmbedBuilder errorEmbed = new EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("An error occurred!");
        try {
            File file = new File("input.nbs");
            System.out.println(event.getMessage().getAttachments().get(0).getFileExtension());
            CompletableFuture.runAsync(() -> {
                CompletableFuture<File> task = attachment.downloadToFile(file);
                while(!task.isDone()) { }
                try {
                    String code = new NBSToTemplate(NBSDecoder.parse(file)).convert();
                    byte[] b64 = CompressionUtil.toBase64(CompressionUtil.toGZIP(code.getBytes(StandardCharsets.UTF_8)));
                    String jsonInputString = String.format("{\"template\": \"%s\",\"temp\": true}",new String(b64));
                    JsonObject json = new Gson().fromJson(generateLink(jsonInputString),JsonObject.class);

                    EmbedBuilder embed = new EmbedBuilder()
                        .setColor(new Color(70,199,82))
                        .setTitle("Function Generated!")
                        .setThumbnail("https://static.wikia.nocookie.net/minecraft/images/9/9b/Note_Block.png/revision/latest?cb=20190921170620")
                        .addField("Link: __Expires in 2 mins__","[Function Link](https://derpystuff.gitlab.io/code/l?link=" + json.get("link").getAsString() + ")",false)
                        .addField("Info:","Click the link shown above and click the button in the bottom left corner to copy the give command for the template. You will need [this function](https://derpystuff.gitlab.io/code/l?link=7cf5d91c35bbde31c28567d8d8945c40) to play songs.",false);

                    channel.sendMessage(embed.build()).queue();
                } catch(OutdatedNBSException e) {
                    e.printStackTrace();
                    channel.sendMessage(errorEmbed.build()).queue();
                } catch(IOException e) {
                    e.printStackTrace();
                    channel.sendMessage(errorEmbed.build()).queue();
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
            channel.sendMessage(errorEmbed.build()).queue();
        }

    }
    private static String generateLink(String templateData) throws IOException {
        URL url = new URL("https://twv.vercel.app/v2/create");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        byte[] out = templateData.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.setRequestProperty("Accept","application/json; charset=UTF-8");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
        Scanner s = new Scanner(http.getInputStream()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        s.close();
        http.disconnect();

        return result;
    }
}
