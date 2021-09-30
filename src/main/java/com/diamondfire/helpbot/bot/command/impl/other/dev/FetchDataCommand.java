package com.diamondfire.helpbot.bot.command.impl.other.dev;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MultiArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.CommandCategory;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.util.PlainComponentSerializer;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class FetchDataCommand extends Command {
    
    private static final String USERNAME = HelpBotInstance.getConfig().getMcEmail();
    private static final String PASSWORD = HelpBotInstance.getConfig().getMcPassword();
    
    @Override
    public String getName() {
        return "fetch";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Fetches data from node beta, then saves it as the current db.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("flag",
                        new MultiArgumentContainer<>(
                                new StringArgument()
                        ).optional(null));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }
    
    // TODO: flag code
    @Override
    public void run(CommandEvent event) {
        List<String> flags = event.getArgument("flag");
        if (flags == null) {
            setup(event.getChannel());
        } else {
            boolean includeColors = false;
            boolean updateDb = true;
            for (String flag : flags) {
                if (flag.equals("-c")) {
                    includeColors = true;
                } else if (flag.equals("-d")) {
                    updateDb = false;
                }
            }
            setup(event.getChannel(), includeColors, updateDb);
        }
    }
    
    public void setup(TextChannel channel) {
        setup(channel, false, true);
    }
    
    public void setup(TextChannel channel, boolean includeColors, boolean updateDb) {
        EmbedBuilder builder = new EmbedBuilder();
        
        builder.setTitle("Fetching Code Database...");
        builder.setDescription("Please wait a moment!");
        
        Message sentMessage = channel.sendMessageEmbeds(builder.build()).complete();
        
        fetchData(sentMessage, includeColors).thenAccept((queue) -> {
            status(sentMessage, String.format("Data has been received, parsing %s lines...", queue.size()));
            
            File file = null;
            try {
                file = updateDb ? ExternalFiles.DB : ExternalFileUtil.generateFile("temp_db.txt");
                if (updateDb) {
                    CodeDifferenceHandler.setComparer(file);
                }
                
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getPath(), true))) {
                    for (String s : queue) {
                        bufferedWriter.append(s);
                    }
                    
                }
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (updateDb) {
                status(sentMessage, "Restarting and comparing code database...");
                CodeDatabase.initialize();
                CodeDifferenceHandler.refresh();
                status(sentMessage, "Finished!");
            } else {
                status(sentMessage, "Finished!");
                sentMessage.getChannel().sendFile(file).queue();
            }
        }).exceptionally((exception) -> {
            error(sentMessage, exception);
            
            return null;
        });
    }
    
    private CompletableFuture<List<String>> fetchData(Message message, boolean includeColors) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();
        
        AuthenticationService authService = new AuthenticationService();
        authService.setUsername(USERNAME);
        authService.setPassword(PASSWORD);
        try {
            authService.login();
        } catch (RequestException e) {
            e.printStackTrace();
        }
        
        MinecraftProtocol protocol = new MinecraftProtocol(authService.getSelectedProfile(), authService.getAccessToken());
        TcpClientSession client = new TcpClientSession("beta.mcdiamondfire.com", 25565, protocol);
        
        status(message, "Connecting to DiamondFire...");
        
        client.connect();
        
        client.addListener(new SessionAdapter() {
            
            boolean ready = false;
            List<String> queue = new ArrayList<>();
            
            @Override
            public void packetReceived(PacketReceivedEvent event) {
                Packet packet = event.getPacket();
                
                if (packet instanceof ServerJoinGamePacket) {
                    status(message, "Joined server!");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    event.getSession().send(new ClientChatPacket("/chat none"));
                    event.getSession().send(new ClientChatPacket(includeColors ? "/dumpactioninfo -c" : "/dumpactioninfo"));
                }
                
                if (packet instanceof ServerChatPacket) {
                    ServerChatPacket chatPacket = event.getPacket();
                    String text = PlainComponentSerializer.INSTANCE.serialize(chatPacket.getMessage());
                    
                    if (chatPacket.getType() == MessageType.NOTIFICATION) return;
                    
                    if (text.contains("Unknown command!")) {
                        completableFuture.completeExceptionally(new IllegalStateException("Command not found!"));
                    }
                    
                    if (text.startsWith("{")) {
                        status(message, "Receiving data...");
                        ready = true;
                    } else if (text.startsWith("}")) {
                        client.disconnect("HelpBot data collection has concluded. ");
                        
                        if (queue.isEmpty()) {
                            completableFuture.completeExceptionally(new IllegalStateException("Failed to retrieve data"));
                            return;
                        }
    
                        queue.add("}");
                        completableFuture.complete(queue);
                        return;
                    }
                    
                    if (ready) {
                        queue.add(new String(text.getBytes(StandardCharsets.UTF_8)));
                    }
                    
                }
            }
        });
        
        return completableFuture;
    }
    
    private void error(Message message, Throwable e) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Error occurred!");
        builder.setDescription(e.getMessage());
        message.editMessageEmbeds(builder.build()).queue();
        e.printStackTrace();
    }
    
    private void status(Message message, String status) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Status");
        builder.setDescription(status);
        message.editMessageEmbeds(builder.build()).queue();
    }
    
}
