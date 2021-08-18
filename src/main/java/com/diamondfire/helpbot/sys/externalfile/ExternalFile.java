package com.diamondfire.helpbot.sys.externalfile;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ExternalFile extends File {
    
    public ExternalFile(@NotNull String pathname) {
        super(pathname);
    }
    
    public String read() throws IOException {
        return new String(Files.readAllBytes(toPath()));
    }
    
    public List<String> readAllLines() throws IOException {
        return Files.readAllLines(toPath());
    }
    
    public void write(byte[] content) throws IOException {
        delete();
        createNewFile();
        
        Files.write(toPath(), content, StandardOpenOption.WRITE);
    }
    
    public void write(String content) throws IOException {
        write(content.getBytes());
    }
    
    public void write(JsonObject json) throws IOException {
        write(json.toString());
    }
    
    public String getExtension() {
        return getPath().replaceFirst("^.*\\.", "");
    }
    
    public JsonObject parseJson() throws IOException {
        if (!getExtension().equals(ExternalFileType.JSON.getExtension())) {
            throw new UnsupportedOperationException();
        }
        
        return JsonParser.parseString(read()).getAsJsonObject();
    }
    
    public void copy(File file) throws IOException {
        copy(file.toPath());
    }
    
    public void copy(Path path) throws IOException {
        Files.copy(toPath(), path, StandardCopyOption.REPLACE_EXISTING);
    }
}
