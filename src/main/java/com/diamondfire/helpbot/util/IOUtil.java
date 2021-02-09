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
    
    /**
     * Decompresses GZIP data
     *
     * @param str GZIPPED data
     * @return Decompressed Data
     * @throws IOException Can throw error
     */
    
    public static byte[] fromGZIP(byte[] str) throws IOException {
        // Converts from gzip
        BufferedReader bf = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(str)), StandardCharsets.UTF_8));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            outStr.append(line);
        }
        bf.close();
        return outStr.toString().getBytes();
        
    }
    
    /**
     * Compresses data into GZIP
     *
     * @param str Data to be GZipped
     * @return Compressed Data
     * @throws IOException Can throw error
     */
    public static byte[] toGZIP(byte[] str) throws IOException {
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str);
        gzip.close();
        obj.close();
        return obj.toByteArray();
    }
    
    
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
    
    
    public static File getFileFromSite(String url, String name) {
        try (InputStream in = new URL(url).openStream();) {
            File tempFile = ExternalFileUtil.generateFile(name);
            
            Files.write(tempFile.toPath(), in.readAllBytes(), StandardOpenOption.WRITE);
            
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

