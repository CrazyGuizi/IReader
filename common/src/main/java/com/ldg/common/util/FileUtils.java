package com.ldg.common.util;

import java.io.File;

public class FileUtils {

    public static final String SUFFIX = ".ldg";


    public static File createFile(String path) {
        File file = new File(path);
        if (!isExit(path)) {
            file.mkdirs();
        }

        return file;
    }

    public static boolean isExit(String path) {
        File file = new File(path);
        return file.exists();
    }
}
