package com.l3cache.snapshop.volley;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

public class LocalCache extends LruCache<String, Bitmap> implements ExtendedImageLoader.ImageCache {

	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		return cacheSize;
	}

	public LocalCache() {
		this(getDefaultLruCacheSize());
	}

	public LocalCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}

	@Override
	public Bitmap getBitmap(String key) {
		if (key.contains("file://")) {
			return BitmapFactory.decodeFile(key.substring(key.indexOf("file://") + 7));
		} else {
			// Here you can add an actual cache
			return null;
		}
	}
}
