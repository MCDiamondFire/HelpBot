package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.components.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.components.externalfile.ExternalFile;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.instance.BotInstance;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.protocol.*;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.packetlib.*;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.io.*;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class FetchDataCommand extends Command {

    private static final String USERNAME = BotInstance.getConfig().getMcEmail();
    private static final String PASSWORD = BotInstance.getConfig().getMcPassword();
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
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {
        setup(event.getChannel());
    }

    public void setup(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Fetching Code Database...");
        builder.setDescription("Please wait a moment!");

        Message sentMessage = channel.sendMessage(builder.build()).complete();

        try {
            start(sentMessage);
        } catch (Exception exception) {
            error(sentMessage, exception);
            return;
        }

        status(sentMessage, String.format("Data has been received, parsing %s lines...", queue.size()));

        File file = ExternalFile.DB.getFile();

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
        MinecraftProtocol protocol = new MinecraftProtocol(USERNAME, PASSWORD);
        Client client = new Client("beta.mcdiamondfire.com", 25565, protocol, new TcpSessionFactory());
        Session session = client.getSession();

        session.setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        status(message, "Connecting to DiamondFire...");
        ready = false;

        client.getSession().connect();
        client.getSession().addListener(new SessionAdapter() {
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
                    String text = chatPacket.getMessage().getFullText();
                    if (chatPacket.getType() == MessageType.NOTIFICATION) return;

                    if (text.contains("Unknown command!")) {
                        throw new IllegalStateException("Command not found!");
                    }

                    if (text.startsWith("{")) {
                        status(message, "Receiving data...");
                        ready = true;
                    } else if (text.startsWith("}")) {
                        session.disconnect("HelpBot data collection has concluded. ");
                    }

                    if (ready) {
                        queue.add(new String(text.getBytes(StandardCharsets.UTF_8)));
                    }


                }
            }
        });

        while (session.isConnected()) {
            try {
                Thread.sleep(5);
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
        message.editMessage(builder.build()).queue();
        e.printStackTrace();
    }

    private void status(Message message, String status) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Status");
        builder.setDescription(status);
        message.editMessage(builder.build()).queue();
    }

}
