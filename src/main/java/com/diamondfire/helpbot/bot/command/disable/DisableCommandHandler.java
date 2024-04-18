package com.diamondfire.helpbot.bot.command.disable;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DisableCommandHandler {
    
    private final List<String> disabledCommands = new ArrayList<>();
    private static final File FILE = ExternalFiles.DISABLED_COMMANDS;
    
    public boolean isDisabled(Command command) {
        return disabledCommands.contains(command.getName());
    }
    
    public void initialize() {
        try {
            List<String> lines = Files.readAllLines(FILE.toPath());
            for (String cmd : lines) {
                disable(CommandHandler.getCommand(cmd));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void save() {
        String string = String.join("\n", disabledCommands);
        try {
            FILE.delete();
            FILE.createNewFile();
            Files.write(FILE.toPath(), string.getBytes(), StandardOpenOption.WRITE);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public void enable(Command command) {
        disabledCommands.remove(command.getName());
        save();
    }
    
    public void disable(Command command) {
        if (command == null) {
            return;
        }
        // Prevents tricky people using eval.
        if (command instanceof CommandDisableFlag) {
            return;
        }
        
        disabledCommands.add(command.getName());
        save();
    }
    
    
    public List<String> getDisabledCommands() {
        return disabledCommands;
    }
}


