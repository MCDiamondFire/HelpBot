package com.diamondfire.helpbot.sys.disablecmds;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.sys.externalfile.ExternalFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DisableCommandHandler {

    private static final List<String> disabledCommands = new ArrayList<>();
    private static final File disabledCommandsFile = ExternalFile.DISABLED_COMMANDS.getFile();

    public static boolean isDisabled(Command command) {
        return disabledCommands.contains(command.getName());
    }

    public static void initialize() {
        try {
            List<String> lines = Files.readAllLines(ExternalFile.DISABLED_COMMANDS.getFile().toPath());
            for (String cmd : lines) {
                    disable(CommandHandler.getCommand(cmd));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void save() {
        String string = String.join("\n", disabledCommands);
        try {
            disabledCommandsFile.delete();
            disabledCommandsFile.createNewFile();
            Files.write(disabledCommandsFile.toPath(), string.getBytes(), StandardOpenOption.WRITE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void enable(Command command) {
        disabledCommands.remove(command.getName());
        DisableCommandHandler.save();
    }

    public static void disable(Command command) {
        disabledCommands.add(command.getName());
        DisableCommandHandler.save();
    }


    public static List<String> getDisabledCommands() {
        return disabledCommands;
    }
}


