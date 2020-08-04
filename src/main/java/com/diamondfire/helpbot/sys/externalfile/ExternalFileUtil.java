package com.diamondfire.helpbot.sys.externalfile;

import java.io.*;

public class ExternalFileUtil {

    public static File generateFile(String name) throws IOException {

        File file = new File(ExternalFile.OTHER_CACHE_DIR.getFile(), name);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    public static File getFile(String name) {
        return new File(ExternalFile.OTHER_CACHE_DIR.getFile(), name);
    }
}
