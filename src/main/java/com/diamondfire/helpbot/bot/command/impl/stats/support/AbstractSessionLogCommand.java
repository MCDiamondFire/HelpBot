package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.Player;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.nio.file.*;
import java.util.*;

public abstract class AbstractSessionLogCommand extends AbstractPlayerUUIDCommand {
    
    abstract protected List<Session> getSessions(String player);
    
    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }
    
    @Override
    protected void execute(CommandEvent event, Player player) {
        List<Session> sessions = getSessions(player.name());
        StringBuilder builder = new StringBuilder();
        
        for (Session session : sessions) {
            builder.append(FormatUtil.formatDate(session.getTaken()) + " ");
            builder.append(session.getSupportee() + " ");
            builder.append('(' + FormatUtil.formatMilliTime(session.getDuration()) + ')');
            
            builder.append("\n");
        }
        
        try {
            File file = ExternalFileUtil.generateFile("session_log.txt");
            Files.writeString(file.toPath(), builder.toString(), StandardOpenOption.WRITE);
            
            event.getChannel().sendFiles(FileUpload.fromData(file)).queue();
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        
    }
    
    static class Session {
        
        String supportee;
        long duration;
        Date taken;
        
        public Session(String supportee, long duration, Date taken) {
            this.supportee = supportee;
            this.duration = duration;
            this.taken = taken;
        }
        
        public String getSupportee() {
            return supportee;
        }
        
        public long getDuration() {
            return duration;
        }
        
        public Date getTaken() {
            return taken;
        }
        
    }
    
}
