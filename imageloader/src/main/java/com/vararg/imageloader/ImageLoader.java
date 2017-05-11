package com.vararg.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.vararg.imageloader.cache.BitmapMemoryCache;
import com.vararg.imageloader.cache.FileDiskCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by vararg on 10.05.2017.
 */

public class ImageLoader {

    static final String TAG = "ImageLoader";

    // Thread pool size for image loading
    private static final int THREAD_POOL_SIZE = 5;
    private static final int EFFICIENT_HEIGHT = 200;
    private static final int EFFICIENT_WIDTH = 200;
    private static final int CONNECTION_TIMEOUT = 30000;

    private static ImageLoader instance;

    private ExecutorService executorService;

    // Container for checking reusable relationsMap
    private Map<ImageView, String> relationsMap = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());

    // Handler to display images in UI thread
    private Handler handler = new Handler();

    private FileDiskCache diskCache;
    private BitmapMemoryCache memoryCache;

    private ImageLoader(Context context) {
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        diskCache = new FileDiskCache(context);
        memoryCache = new BitmapMemoryCache();
    }

    // Lazy threadsafe singleton
    public static ImageLoader with(Context context) {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader(context);
                }
            }
        }

        return instance;
    }

    public void loadTo(String url, ImageView imageView) {
        // Put image view with url to avoid collisions with reusable relationsMap
        relationsMap.put(imageView, url);

        // If image already cached just show it
        Bitmap bitmap = memoryCache.getData(url);
        if (bitmap != null) {
            Log.d("HIHO", "bitmap from memory");
            imageView.setImageBitmap(bitmap);
        } else {
            loadAndDisplay(url, imageView);
        }
    }

    public void clearCache(Context context) {
        memoryCache.clear();
        diskCache.clear();
    }

    private void loadAndDisplay(String url, ImageView imageView) {
        // Create data holder for current loading
        LoadDataHolder loadDataHolder = new LoadDataHolder(url, imageView);

        // Add placeholder while load image
        imageView.setImageResource(R.color.placeholder);

        // Execute loading runnable
        executorService.submit(new ImageLoadRunnable(loadDataHolder));
    }

    private Bitmap getBitmap(String url) {
        // Check is file already cached
        File file = diskCache.getData(url);
        Bitmap bitmap = decode(file);
        if (bitmap == null) {
            // If no then load it
            bitmap = loadAndCacheBitmap(file, url);
        } else {
            Log.d("HIHO", "bitmap from memory");
        }

        return bitmap;
    }

    // Download image file from web and save to file
    private Bitmap loadAndCacheBitmap(File cacheFile, String url) {
        File tempFile = new File(cacheFile.getPath() + "temp_file");
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        OutputStream tempOs = null;
        OutputStream cacheOs = null;

        try {
            URL imageUrl = new URL(url);

            // Create connection
            connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.setInstanceFollowRedirects(true);

            // Save image to temp file
            InputStream is = connection.getInputStream();
            tempOs = new FileOutputStream(tempFile);
            StreamUtils.copy(is, tempOs);

            Log.d("HIHO", "bitmap from web");

            // Decode and scale image to reduce memory consumption
            bitmap = decodeAndScale(tempFile);

            // Save scaled bitmap to cache file
            cacheOs = new FileOutputStream(cacheFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, cacheOs);

        } catch (IOException e) {
            Log.e(TAG, "Error while image was loaded and stored", e);
            // Remove cached file if something go wrong
            cacheFile.delete();
        } finally {
            StreamUtils.closeQuietly(tempOs);
            StreamUtils.closeQuietly(cacheOs);
            // Remove temp file
            tempFile.delete();
            if (connection != null) connection.disconnect();
        }

        return bitmap;
    }

    private Bitmap decodeAndScale(File file) {
        return EfficientBitmapDecoder.decodeAndScaleFile(file, memoryCache, EFFICIENT_WIDTH, EFFICIENT_HEIGHT);
    }

    private Bitmap decode(File file) {
        return EfficientBitmapDecoder.decode(file, memoryCache);
    }

    // Used for avoiding collisions with reusable imageViews or multiple loads
    private boolean imageViewReused(LoadDataHolder loadDataHolder) {
        String url = relationsMap.get(loadDataHolder.getImageView());
        // Check is url exist in relationsMap and equals to url from holder
        return url == null || !url.equals(loadDataHolder.getUrl());
    }

    // Used to display bitmap in the UI thread
    private class ImageLoadRunnable implements Runnable {
        LoadDataHolder loadDataHolder;

        ImageLoadRunnable(LoadDataHolder loadDataHolder) {
            this.loadDataHolder = loadDataHolder;
        }

        @Override
        public void run() {

            // Check if image already downloaded
            if (imageViewReused(loadDataHolder))
                return;
            // Download image from web url
            Bitmap bitmap = getBitmap(loadDataHolder.getUrl());

            // Save to cache
            if (bitmap != null) {
                memoryCache.put(loadDataHolder.getUrl(), bitmap);
            }

            if (imageViewReused(loadDataHolder))
                return;

            // Get bitmap to display
            BitmapDisplayRunnable displayRunnable =
                    new BitmapDisplayRunnable(bitmap, loadDataHolder);

            // Post message to handler associated with UI thread
            handler.post(displayRunnable);
        }
    }

    // Used to display bitmap in the UI thread
    private class BitmapDisplayRunnable implements Runnable {
        private Bitmap bitmap;
        private LoadDataHolder loadDataHolder;

        BitmapDisplayRunnable(Bitmap bitmap, LoadDataHolder loadDataHolder) {
            this.bitmap = bitmap;
            this.loadDataHolder = loadDataHolder;
        }

        public void run() {
            if (!imageViewReused(loadDataHolder)) {
                // Remove relation for avoiding multiple loads
                relationsMap.remove(loadDataHolder.getImageView());

                // Show bitmap on UI
                if (bitmap != null)
                    loadDataHolder.getImageView().setImageBitmap(bitmap);
                else
                    loadDataHolder.getImageView().setImageResource(R.color.error);
            }
        }
    }
}
