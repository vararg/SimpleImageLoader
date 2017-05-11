package com.vararg.imageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by vararg on 11.05.2017.
 */

public interface BitmapPool {

    Bitmap getReusableBitmap(BitmapFactory.Options options);
}
