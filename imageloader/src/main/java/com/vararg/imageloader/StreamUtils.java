package com.vararg.imageloader;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vararg on 10.05.2017.
 */

class StreamUtils {
    private final static int BUF_SIZE = 1024;

    // Inspired by Guava's ByteStreams.copy()
    // google.github.io/guava/releases/19.0/api/docs/src-html/com/google/common/io/ByteStreams.html#line.103
    static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[BUF_SIZE];

        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
        }

    }

    static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.w(ImageLoader.TAG, "Error while stream closing", e);
            }
        }
    }
}
