package com.diamondfire.helpbot.bot.command.impl.other.dev;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.util.PlainComponentSerializer;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class FetchDataCommand extends Command {
    
    private static final String USERNAME = HelpBotInstance.getConfig().getMcEmail();
    private static final String PASSWORD = HelpBotInstance.getConfig().getMcPassword();
    private final ArrayList<String> queue = new ArrayList<>();
    private boolean ready = false;
    
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
        return new ArgumentSet();
    }
    
    @Override
    public Rank getRank() {
        return Rank.BOT_DEVELOPER;
    }
    
    @Override
    public void run(CommandEvent event) {
        setup(event.getChannel());
    }
    
    public void setup(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        
        builder.setTitle("Fetching Code Database...");
        builder.setDescription("Please wait a moment!");
        
        Message sentMessage = channel.sendMessageEmbeds(builder.build()).complete();
        
        try {
            start(sentMessage);
        } catch (Exception exception) {
            error(sentMessage, exception);
            return;
        }
        
        status(sentMessage, String.format("Data has been received, parsing %s lines...", queue.size()));
        
        File file = ExternalFiles.DB;
        
        CodeDifferenceHandler.setComparer(file);
        
        try {
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
        status(sentMessage, "Restarting and comparing code database...");
        CodeDatabase.initialize();
        CodeDifferenceHandler.refresh();
        status(sentMessage, "Finished!");
        
        queue.clear();
    }
    
    private void start(Message message) throws RequestException {
        AuthenticationService authService = new AuthenticationService();
        authService.setUsername(USERNAME);
        authService.setPassword(PASSWORD);
        authService.login();
    
        MinecraftProtocol protocol = new MinecraftProtocol(authService.getSelectedProfile(), authService.getAccessToken());
        TcpClientSession client = new TcpClientSession("beta.mcdiamondfire.com", 25565, protocol);
        
        status(message, "Connecting to DiamondFire...");
        ready = false;
        
        client.connect();
        client.addListener(new SessionAdapter() {
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
                    event.getSession().send(new ClientChatPacket("/dumpactioninfo"));
                }
                
                if (packet instanceof ServerChatPacket) {
                    ServerChatPacket chatPacket = event.getPacket();
                    String text = PlainComponentSerializer.INSTANCE.serialize(chatPacket.getMessage());
                    
                    if (chatPacket.getType() == MessageType.NOTIFICATION) return;
                    
                    if (text.contains("Unknown command!")) {
                        throw new IllegalStateException("Command not found!");
                    }
                    
                    if (text.startsWith("{")) {
                        status(message, "Receiving data...");
                        ready = true;
                    } else if (text.startsWith("}")) {
                        client.disconnect("HelpBot data collection has concluded. ");
                    }
                    
                    if (ready) {
                        queue.add(new String(text.getBytes(StandardCharsets.UTF_8)));
                    }
                    
                }
            }
        });
        
        while (client.isConnected()) {
            try {
                Thread.sleep(1);
            } catch (Exception ignored) {
            }
        }
        
        if (queue.isEmpty()) {
            throw new IllegalStateException("Failed to retrieve data");
        }
        
    }
    
    private void error(Message message, Exception e) {
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
