package com.diamondfire.helpbot.sys.externalfile;

import java.io.*;
import java.nio.file.*;

public class ExternalFileBuilder {
    
    String fileName;
    boolean directory = false;
    
    public ExternalFileBuilder setName(String fileName) {
        this.fileName = fileName;
        return this;
    }
    
    public ExternalFileBuilder isDirectory(boolean directory) {
        this.directory = directory;
        return this;
    }
    
    protected Path buildRaw() throws IOException {
        Path path = Path.of(fileName);
    
        if (directory) {
            try {
                Files.createDirectory(path);
            } catch (FileAlreadyExistsException x) {
                if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) throw x;
            }
        } else {
            try {
                Files.createFile(path);
            } catch (FileAlreadyExistsException x) {
                if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) throw x;
            }
        }
        
        return path;
    }
    
    public Path build() {
        try {
            return buildRaw();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
