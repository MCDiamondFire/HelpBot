package com.owen1212055.helpbot.command.commands;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.owen1212055.helpbot.components.codedatabase.CodeDifferenceHandler;
import com.owen1212055.helpbot.components.codedatabase.db.CodeDatabase;
import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.arguments.NoArg;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.events.CommandEvent;
import com.owen1212055.helpbot.components.ExternalFileHandler;
import com.owen1212055.helpbot.util.SensitiveData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class FetchDataCommand extends Command {

    private static final String USERNAME = SensitiveData.USER_EMAIL;
    private static final String PASSWORD = SensitiveData.USER_PASS;


    boolean ready = false;


    private Message sentMessage;
    private ArrayList<String> queue = new ArrayList<>();
    private BufferedWriter bufferedWriter = null;

    boolean errored = false;


    @Override
    public String getName() {
        return "fetch";
    }

    @Override
    public String getDescription() {
        return "Fetches data from node beta.";
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
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

        sentMessage = channel.sendMessage(builder.build()).complete();
        login(sentMessage);

        if (errored) {
            return;
        }

        status(sentMessage, String.format("Data has been received, parsing %s lines...", queue.size()));

        File file = ExternalFileHandler.DB;

        CodeDifferenceHandler.setComparer(file);

        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }

            bufferedWriter = new BufferedWriter(new FileWriter(file.getPath(), true));

            for (String s : queue) {
                bufferedWriter.append(s);
            }

            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        status(sentMessage, "Restarting code database...");
        CodeDatabase.initialize();
        status(sentMessage, "Comparing to last database...");
        CodeDifferenceHandler.refresh();
        status(sentMessage, "Finished!");



    }

    private void login(Message message) {
        MinecraftProtocol protocol = null;
        try {
            protocol = new MinecraftProtocol(USERNAME, PASSWORD);
        } catch (RequestException e) {
            error(message, e);
            errored = true;
            return;
        }

        Client client = new Client("beta.mcdiamondfire.com", 25565, protocol, new TcpSessionFactory());
        client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);

        status(message, "Connecting to DiamondFire...");
        client.getSession().connect();

        client.getSession().addListener(new SessionAdapter() {
            @Override
            public void packetReceived(PacketReceivedEvent event) {
                if (event.getPacket() instanceof ServerJoinGamePacket) {
                    status(message, "Joined server!");
                    event.getSession().send(new ClientChatPacket("/chat none"));
                    event.getSession().send(new ClientChatPacket("/dumpactioninfo"));
                }
                if (event.getPacket() instanceof ServerChatPacket) {
                    if (((ServerChatPacket) event.getPacket()).getType() == MessageType.NOTIFICATION) {
                        return;
                    }
                    String text = ((ServerChatPacket) event.getPacket()).getMessage().getFullText();
                    if (text.startsWith("{")) {
                        status(message, "Receiving data...");
                        ready = true;
                    }
                    if (ready) {
                            queue.add(new String(text.getBytes(StandardCharsets.UTF_8)));
                    }
                    if (text.startsWith("}")) {
                        event.getSession().disconnect("Sorry y'all, but ima head out.");
                        status(message, "Database cloned!");
                    }

                    if (text.contains("Unknown command!")) {
                        error(message, new Exception("Command was not found!"));
                        errored = true;
                        event.getSession().disconnect("ERRORED");
                        return;
                    }

                }
            }
        });

        while (client.getSession().isConnected()) {
            try {
                Thread.sleep(5);
            } catch (Exception e) {

            }
        }

    }

    private void error(Message message, Exception e) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Error occured!");
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
