package com.vararg.imageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Created by vararg on 11.05.2017.
 *
 * Efficient memory caching with reusable bitmap pool
 * Inspired by https://developer.android.com/topic/performance/graphics/manage-memory.html
 */

public class BitmapMemoryCache implements Cache<String, Bitmap>, BitmapPool {

    private LruCache<String, Bitmap> cache;

    // Reusable bitmaps
    private final Set<SoftReference<Bitmap>> bitmapPool;

    public BitmapMemoryCache() {

        // Allocate 1/4 of heap size
        int cacheSize = (int) Runtime.getRuntime().maxMemory() / 4;
        Log.d("HIHO", "memory cache size = " + cacheSize);
        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                bitmapPool.add(new SoftReference<>(oldValue));
            }
        };

        bitmapPool = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
    }

    @Override
    public Bitmap getData(String key) {
        return cache.get(key);
    }

    @Override
    public void clear() {
        // Clear cache
        cache.evictAll();
    }

    @Override
    public Collection<SoftReference<Bitmap>> getReusableBitmaps() {
        return bitmapPool;
    }

    // This method iterates through the reusable bitmaps, looking for one to use for inBitmap:
    @Override
    public Bitmap getReusableBitmap(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (bitmapPool != null && !bitmapPool.isEmpty()) {
            synchronized (bitmapPool) {
                final Iterator<SoftReference<Bitmap>> iterator = bitmapPool.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap.
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again.
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }

        Log.d("HIHO", "bitmap reused = " + (bitmap != null));
        Log.d("HIHO", "bitmap pool size = " + bitmapPool.size());
        return bitmap;
    }

    public void put(String key, Bitmap bitmap) {
        cache.put(key, bitmap);
    }

    private static boolean canUseForInBitmap(Bitmap candidate,
                                             BitmapFactory.Options targetOptions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }

        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }

    //A helper function to return the byte usage per pixel of a bitmap based on its configuration.
    private static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
}
