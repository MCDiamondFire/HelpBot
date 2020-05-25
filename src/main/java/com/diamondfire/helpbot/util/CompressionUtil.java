package com.diamondfire.helpbot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil {


    /**
     * Decompresses Base64
     *
     * @param Base64F Compressed Data
     * @return Decompressed Data
     */
    public static byte[] fromBase64(byte[] Base64F) {
        return Base64.getDecoder().decode(Base64F);
    }


    /**
     * Compresses Base64
     *
     * @param Base64F Data
     * @return Compressed Data
     */
    public static byte[] toBase64(byte[] Base64F) {
        return Base64.getEncoder().encode(Base64F);
    }

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


}

