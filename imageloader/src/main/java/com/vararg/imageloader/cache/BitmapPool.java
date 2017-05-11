package com.vararg.imageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.SoftReference;
import java.util.Collection;

/**
 * Created by vararg on 11.05.2017.
 */

public interface BitmapPool {
    Collection<SoftReference<Bitmap>> getReusableBitmaps();

    Bitmap getReusableBitmap(BitmapFactory.Options options);
}
