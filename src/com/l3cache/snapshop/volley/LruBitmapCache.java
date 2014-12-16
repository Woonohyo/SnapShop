package com.l3cache.snapshop.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v4.util.LruCache;
import android.util.Log;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.l3cache.snapshop.SnapPreference;

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {
	private static Context context;

	public static int getDefaultLruCacheSize(Context theContext) {
		context = theContext;
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		return cacheSize;
	}

	public LruBitmapCache() {
		this(getDefaultLruCacheSize(null));
	}

	public LruBitmapCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	public LruBitmapCache(Context applicationContext) {
		this(getDefaultLruCacheSize(applicationContext));
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		SnapPreference pref = new SnapPreference(context);
		if (url.contains("file://")) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			Bitmap bitmap = BitmapFactory.decodeFile(url.substring(url.indexOf("file://") + 7), options);
			bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
			int rotation = pref.getValue(SnapPreference.PREF_EXIF_ORIENTATION, 1);
			int rotationInDegrees = exifToDegrees(rotation);
			if (rotation != 0f) {
				bitmap = rotate(bitmap, rotationInDegrees);
			}
			return bitmap;
		} else {
			return get(url);
		}
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}

	private static int exifToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	public Bitmap rotate(Bitmap bitmap, int degrees) {
		Log.i("UploadSnapView", "Rotating by - " + degrees);
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

			try {
				Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != converted) {
					bitmap.recycle();
					bitmap = converted;
				}
			} catch (OutOfMemoryError ex) {
				// 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
			}
		}
		return bitmap;
	}
}