package com.diamondfire.helpbot.components.externalfile;

import java.io.File;
import java.io.IOException;

public class ExternalFileUtil {

    public static File generateFile(String name) throws IOException {

        File file = new File(ExternalFile.OTHER_CACHE_DIR.getFile(), name);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }
}
