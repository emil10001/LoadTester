package com.feigdev.loadtester;

import android.util.Log;

/**
 * Created by ejf3 on 11/4/13.
 */
public class PiCalc {
    public static final String TAG = "PiCalc";

    public static void slowCalc() {
        double sum = 0.0;      // final sum
        double term;           // term without sign
        double sign = 1.0;     // sign on each term
        long endTime = System.currentTimeMillis() + Constants.MODE.CPU_ON_TIME;
        int k = 0;
        while (System.currentTimeMillis() < endTime) {
            term = 1.0 / (2.0 * k + 1.0);
            sum = sum + sign * term;
            sign = -sign;
            k++;
        }
        BusProvider.INSTANCE.bus().post(new MessageTypes.CpuStatus(Thread.currentThread().getId()
                + ": Final pi (approx., " + k + " terms): " + sum * 4.0));
        Log.d(TAG, "Final pi (approx., " + k + " terms): " + sum * 4.0);
        Log.d(TAG, "Actual pi: " + Math.PI);
    }

    public static class CalcTask implements Runnable {
        @Override
        public void run() {
            try {
                Log.d(TAG, "start running");
                PiCalc.slowCalc();
                if (Thread.currentThread().isInterrupted()) {
                    Log.w(TAG, "Exiting gracefully");
                    return;
                }
                Thread.sleep(Constants.MODE.CPU_IDLE_TIME);
            } catch (InterruptedException ex) {
            }
        }
    }
}
