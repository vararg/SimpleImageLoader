package com.vararg.imageloader;

import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by vararg on 11.05.2017.
 *
 * Holder for url and image view, used in runnable
 */

class LoadDataHolder {
    private String url;
    private WeakReference<ImageView> weakReference;

    LoadDataHolder(String url, ImageView imageView) {
        this.url = url;
        this.weakReference = new WeakReference<>(imageView);
    }

    String getUrl() {
        return url;
    }

    ImageView getImageView() {
        return weakReference.get();
    }
}
