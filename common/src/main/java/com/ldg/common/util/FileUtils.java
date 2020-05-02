package com.ldg.common.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.ldg.common.log.LogUtil;

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

    /**
     * 创建目录
     *
     * @param path
     * @return
     */
    public static File createDir(String path) {
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    /**
     * 创建目录
     *
     * @param file
     * @return
     */
    public static File createDir(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    /**
     * 创建文件
     *
     * @param path
     * @return
     */
    public static File createFile(String path) {
        File file = new File(path);
        if (!isExit(path)) {
            file.getParentFile().mkdirs();
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

        LogUtil.d("存储位置：" + file.getAbsolutePath());
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
