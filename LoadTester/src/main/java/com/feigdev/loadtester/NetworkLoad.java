package com.feigdev.loadtester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by ejf3 on 11/24/13.
 */
public class NetworkLoad {
    private static final String TAG = "NetworkLoad";
    private static final String IMG_URL = "https://s3.amazonaws.com/ejf3-public/load_test_app/test.png";

    private static int loadImage() {
        OkHttpClient client = new OkHttpClient();

        // Ignore invalid SSL endpoints.
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        // Create request for remote resource.
        HttpURLConnection connection;
        try {
            connection = client.open(new URL(IMG_URL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return -1;
        }
        InputStream is;
        try {
            is = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        Bitmap bmp = BitmapFactory.decodeStream(is);

        int retVal = ImgLoader.getBitmapSize(bmp);

        Log.d(TAG, "loaded image from network, size: "
                + retVal);

        return retVal;
    }

    public static class NetworkLoaderTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(Constants.MODE.NET_DELAY_TIME);
                int size = loadImage();
                if (-1 == size){
                    Log.w(TAG,"failed to load");
                    BusProvider.INSTANCE.bus().post(new MessageTypes.NetStatus(Thread.currentThread().getId() + " failed to load image"));
                    return;
                }
                BusProvider.INSTANCE.bus().post(new MessageTypes.NetStatus(Thread.currentThread().getId() + " loaded: " + size + " bytes"));
                Thread.sleep(Constants.MODE.NET_IDLE_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
}