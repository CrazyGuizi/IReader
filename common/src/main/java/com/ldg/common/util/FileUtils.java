package com.ldg.common.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    public static final String SUFFIX = ".ldg";


    public static final String getCacheDir(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("the context is null");
        }
        if (isSdCardExit()) {
            return context.getExternalCacheDir().getAbsolutePath();
        }
        return context.getCacheDir().getAbsolutePath();
    }

    public static boolean isSdCardExit() {
        return Environment.isExternalStorageEmulated();
    }

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

    public static String getBookCachePath(Context context, String bookId, String chapterName) {
        return getCacheDir(context) + File.separator +
                MD5Utils.Str2Md5(bookId) + File.separator +
                chapterName + SUFFIX;
    }

    public static void saveFile(Context context, String bookId, String chapterName, String content) {
        saveFile(getBookCachePath(context, bookId, chapterName), content);
    }

    public static void saveFile(String cachePath, String content) {
        File file = createFile(cachePath);

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
