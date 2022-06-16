package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.*;
import java.util.zip.*;

public class IOUtil {
    public static byte[] zipFile(Path path, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(baos);
        try (Stream<Path> walkStream = Files.walk(path)) {
            for (Path innerPath : walkStream.collect(Collectors.toSet())) {
        
                // Ignore parent file, for some reason that's included
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
        }
        zipOutputStream.finish();
        zipOutputStream.close();
        
        return baos.toByteArray();
    }
    
    
    public static Path getFileFromSite(String url, String name) {
        try (InputStream in = new URL(url).openStream()) {
            Path tempFile = ExternalFileUtil.generateFile(name);
            
            Files.write(tempFile, in.readAllBytes(), StandardOpenOption.WRITE);
            
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    public static void webHook(String url, String string) {
        HttpURLConnection connection = null;
        try {
            
            connection = (HttpURLConnection) new URL(url).openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setDoOutput(true);
            
            
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            
            JsonObject object = new JsonObject();
            object.addProperty("content", string);
            outputStream.writeBytes(object.toString());
            outputStream.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

