package com.vararg.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.vararg.imageloader.cache.BitmapPool;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by vararg on 11.05.2017.
 *
 * Bitmap decoder with BitmapPool support
 */
final class EfficientBitmapDecoder {

    static Bitmap decodeAndScaleFile(File file, BitmapPool bitmapPool, int reqWidth, int reqHeight) {

        if (file == null || !file.exists()) return null;

        String filePath = file.getPath();

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        if (reqHeight > 0 && reqWidth > 0)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Needed for reuse bitmaps from pool
        addInBitmapOptions(options, bitmapPool);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);

    }

    static Bitmap decode(File file, BitmapPool bitmapPool) {

        return decodeAndScaleFile(file, bitmapPool, 0, 0);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static void addInBitmapOptions(BitmapFactory.Options options, BitmapPool bitmapPool) {
        // inBitmap only works with mutable bitmaps, so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (bitmapPool != null) {
            // Try to find a bitmap to use for inBitmap.
            Bitmap inBitmap = bitmapPool.getReusableBitmap(options);

            if (inBitmap != null) {
                // If a suitable bitmap has been found, set it as the value of
                // inBitmap.
                options.inBitmap = inBitmap;
            }
        }
    }

}
