package com.diamondfire.helpbot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.*;
//from https://github.com/CodeUtilities/CodeUtilities

public class CompressionUtil {
    
    /**
     * Decodes data from base64
     *
     * @param bytes Data to be encoded
     * @return Base64 encoded data
     */
    public static byte[] fromBase64(byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }
    
    /**
     * Encodes data to base64
     *
     * @param bytes Data to be decoded
     * @return Decoded data
     */
    public static byte[] toBase64(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }
    
    /**
     * Decompresses GZIP data
     *
     * @param bytes GZIPped data
     * @return Decompressed Data
     * @throws IOException Can throw error
     */
    public static byte[] fromGZIP(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            outStr.append(line);
        }

        return outStr.toString().getBytes();
    }
    
    /**
     * Compresses data into GZIP
     *
     * @param bytes Data to be GZIPped
     * @return Compressed Data
     * @throws IOException Can throw error
     */
    public static byte[] toGZIP(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(bytes);
        gzip.close();

        return obj.toByteArray();
    }

}
