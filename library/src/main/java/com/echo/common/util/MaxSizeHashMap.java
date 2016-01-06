package com.echo.common.util;

import java.util.LinkedHashMap;

/**
 * Attention: maybe LruCache from android.util is a better choice
 * Created by jiangecho on 16/1/1.
 */
public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    /**
     *
     * @param maxSize
     * @param accessOrder true, LRU; false, false
     */
    public MaxSizeHashMap(int maxSize, boolean accessOrder) {
        super(maxSize * 10 / 7, 0.7f, true); // LRU
        //super(maxSize * 10 / 7, 0.7f, true); // FIFO
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
