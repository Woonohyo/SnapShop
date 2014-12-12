package com.l3cache.snapshop.photocrop;
public class Log {

    private static final String TAG = "android-crop";

    public static final void e(String msg) {
        android.util.Log.e(TAG, msg);
    }

    public static final void e(String msg, Throwable e) {
        android.util.Log.e(TAG, msg, e);
    }
    
    public static final void i(String msg) {
    	android.util.Log.e(TAG, msg);
    }

}
