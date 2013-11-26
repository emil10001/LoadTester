package com.feigdev.loadtester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ejf3 on 11/25/13.
 */
public class LoadApi extends BroadcastReceiver {
    private static final String KILL_NOW = "KILL_NOW";
    private static final String KEEP_ALIVE = "KEEP_ALIVE";
    private static final String CPU_ENABLED = "CPU_ENABLED";
    private static final String RAM_ENABLED = "RAM_ENABLED";
    private static final String NET_ENABLED = "NET_ENABLED";
    private static final String MODE = "MODE";
    private static final String LOW = "LOW";
    private static final String MEDIUM = "MEDIUM";
    private static final String HIGH = "HIGH";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (null == extras){
            context.startService(new Intent(context.getApplicationContext(), ThreadSpawn.class));
            return;
        }

        for (String key : extras.keySet()) {
            if (null == extras.get(key))
                continue;

            if (KEEP_ALIVE.equals(key)){
                Constants.KEEP_ALIVE = extras.getLong(key);
                Constants.TIMED_KILL = true;
            } else if (CPU_ENABLED.equals(key)) {
                Constants.CPU_ENABLED = extras.getBoolean(key);
            } else if (RAM_ENABLED.equals(key)) {
                Constants.RAM_ENABLED = extras.getBoolean(key);
            } else if (NET_ENABLED.equals(key)) {
                Constants.NET_ENABLED = extras.getBoolean(key);
            } else if (MODE.equals(key)) {
                String value = extras.getString(key);
                if (LOW.equals(value)) {
                    Constants.MODE = Constants.LOW;
                } else if (MEDIUM.equals(value)) {
                    Constants.MODE = Constants.MEDIUM;
                } else if (HIGH.equals(value)) {
                    Constants.MODE = Constants.HIGH;
                }
            } else if (KILL_NOW.equals(key)) {
                ThreadSpawn.stopSpawner();
                ThreadSpawn.killSpawner();
                return;
            }
        }

        context.startService(new Intent(context.getApplicationContext(), ThreadSpawn.class));
    }

}
