package com.diamondfire.helpbot.sys.externalfile;

import java.io.*;
import java.nio.file.*;

public class ExternalFileUtil {
    
    public static Path generateFile(String name) throws IOException {
        Path path = ExternalFiles.OTHER_CACHE_DIR.resolve(name);
        
        Files.write(path, new byte[0]);
        
        return path;
    }
    
    public static Path getFile(String name) {
        return ExternalFiles.OTHER_CACHE_DIR.resolve(name);
    }
}
