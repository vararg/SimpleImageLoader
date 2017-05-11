package com.vararg.imageloader.cache;

/**
 * Created by vararg on 11.05.2017.
 */

interface Cache<S, T> {
    T getData(S key);
    void clear();
}
