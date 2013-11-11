package com.feigdev.loadtester;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ejf3 on 11/5/13.
 */
public class ImgLoader {
    private static final String TAG = "ImgLoader";

    static ConcurrentHashMap<Integer, Bitmap> bitstore = new ConcurrentHashMap<Integer, Bitmap>();
    private static int counter = 0;

    public static Bitmap generatePic() {
        Bitmap bmp = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Resources res = ThreadSpawn.appContext.getResources();
            bmp = BitmapFactory.decodeResource(res, R.drawable.alf, options);
            Log.d(TAG, "generated image");
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "getPic OutOfMemoryError =(");
            return null;
        } catch (Exception e) {
            Log.w(TAG, "getPic blew up =(");
            return null;
        }

        if (null == bmp)
            return null;

        try {
            int width = bmp.getWidth();
            int height = bmp.getHeight();

            int newWidth = 2000;

            float scaleWidth = ((float) newWidth) / width;
            float newHeight = height * scaleWidth;
            float scaleHeight = newHeight / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);

            if (null != bmp2 && (getBitmapSize(bmp) < getBitmapSize(bmp2)))
                bmp = bmp2;

            Log.d(TAG, "generated image 2");
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "getPic2 OutOfMemoryError =(");
        } catch (Exception e) {
            Log.w(TAG, "getPic2 blew up =(");
        }

        return bmp;
    }

    private static int getBitmapSize(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

    private static void loadImage() {
        Bitmap bmp = generatePic();
        if (null == bmp)
            return;

        int place = counter % Constants.NUM_STORED_IMAGES;
        if (null == bitstore)
            bitstore = new ConcurrentHashMap<Integer, Bitmap>();

        try {
            bitstore.put(place, bmp);
            counter++;
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "bitstore OutOfMemoryError =(");
        }
    }

    public static class ImgLoaderTask implements Runnable {
        @Override
        public void run() {
            try {
                Log.d(TAG, "start running");
                loadImage();
                if (Thread.currentThread().isInterrupted()) {
                    Log.d(TAG, "Exiting gracefully");
                    return;
                }
                Thread.sleep(Constants.RAM_IDLE_TIME);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

}
