package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.zip.*;

public class IOUtil {
    public static File zipFile(Path path, String fileName) throws IOException {
        File zipped = ExternalFileUtil.generateFile(fileName);
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipped));
        for (Path innerPath : Files.walk(path).collect(Collectors.toSet())) {
            
            //Ignore parent file, for some reason that's included
            if (innerPath.getParent() == null) continue;
            
            try {
                zipOutputStream.putNextEntry(new ZipEntry(innerPath.toString()));
                byte[] bytes = Files.readAllBytes(innerPath);
                zipOutputStream.write(bytes, 0, bytes.length);
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        zipOutputStream.finish();
        zipOutputStream.close();
        
        return zipped;
    }
}

