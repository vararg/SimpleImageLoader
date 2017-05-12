package com.vararg.imageloader.cache;

import android.content.Context;

import java.io.File;

/**
 * Created by vararg on 11.05.2017.
 */

public class FileDiskCache implements Cache<String, File> {

    //TODO add memory manage

    private File cacheDir;

    public FileDiskCache(Context context) {
        cacheDir = context.getCacheDir();
    }

    @Override
    public synchronized File getData(String url) {
        // Identify images by hashcode
        String filename = String.valueOf(url.hashCode());

        return new File(cacheDir, filename);
    }

    @Override
    public synchronized void clear() {
        // Get file list from cacheDir
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}
